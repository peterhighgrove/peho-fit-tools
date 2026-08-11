package se.peho.fittools.android

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors
import se.peho.fittools.core.Conf
import se.peho.fittools.core.FitFile
import se.peho.fittools.core.MenuRunner

class MainActivity : AppCompatActivity() {

    private lateinit var filePathText: EditText
    private lateinit var statusText: TextView
    private lateinit var commandText: EditText
    private lateinit var commandSpinner: Spinner
    private lateinit var runButton: Button
    private lateinit var pickButton: Button

    private var selectedUri: Uri? = null

    private val pickFileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedUri = it
            filePathText.setText(getDisplayName(it))
            statusText.text = "Selected: ${it.lastPathSegment}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        filePathText = findViewById(R.id.filePathText)
        statusText = findViewById(R.id.statusText)
        commandText = findViewById(R.id.commandText)
        commandSpinner = findViewById(R.id.commandSpinner)
        runButton = findViewById(R.id.runButton)
        pickButton = findViewById(R.id.pickButton)

        val defaultDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        filePathText.setText(defaultDir.absolutePath)
        statusText.text = "Pick a FIT/ZIP file or open from a file manager"

        val menu = MenuRunner(FitFile(), Conf(arrayOf("Android", "0", ""), true))
        val commands = menu.getAvailableCommandKeys().sorted()
        commandSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, commands).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        pickButton.setOnClickListener { pickFileLauncher.launch("*/*") }

        runButton.setOnClickListener {
            val path = filePathText.text.toString().trim()
            if (path.isEmpty()) {
                statusText.text = "Please choose a file first"
                return@setOnClickListener
            }

            if (selectedUri == null) {
                val f = File(path)
                if (!f.exists()) {
                    statusText.text = "File not found: $path"
                    return@setOnClickListener
                }
            }

            runProcessing(path)
        }

        intent?.data?.let { uri ->
            selectedUri = uri
            filePathText.setText(getDisplayName(uri))
            statusText.text = "Opened from app: ${uri.lastPathSegment}"
        }
    }

    private fun runProcessing(path: String) {
        statusText.text = "Processing..."
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val temp = File.createTempFile("fitfix", ".fit")
                val input = if (selectedUri != null) {
                    contentResolver.openInputStream(selectedUri!!)
                } else {
                    File(path).inputStream()
                }

                input?.use { src ->
                    FileOutputStream(temp).use { dst ->
                        src.copyTo(dst)
                    }
                }

                val commandKey = commandSpinner.selectedItem?.toString()?.trim().orEmpty()
                val extraInput = commandText.text.toString().trim()
                val args = arrayOf("Android", "0", temp.absolutePath)
                val conf = Conf(args, true)
                val fitFile = FitFile()
                val menu = MenuRunner(fitFile, conf)
                menu.runSingleCommand(commandKey.ifEmpty { "cpq" }, extraInput)

                val log = fitFile.getTempUpdateLog()
                runOnUiThread { statusText.text = log.take(4000) }
            } catch (e: Exception) {
                runOnUiThread { statusText.text = "Error: ${e.message}" }
            } finally {
                executor.shutdown()
            }
        }
    }

    private fun getDisplayName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && it.moveToFirst()) {
                return it.getString(nameIndex)
            }
        }
        return uri.lastPathSegment ?: ""
    }
}
