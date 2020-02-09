package com.technolovy.android.bayarberapa

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.technolovy.android.bayarberapa.helper.InvoiceManager
import com.technolovy.android.bayarberapa.model.InvoiceItem
import com.technolovy.android.bayarberapa.model.Recipient
import com.technolovy.android.bayarberapa.model.RecipientOrder
import kotlinx.android.synthetic.main.activity_invoice_list_result.*
import kotlinx.android.synthetic.main.activity_tag_people.*
import kotlinx.android.synthetic.main.activity_tag_people.recycler_view

class TagPeople : AppCompatActivity() {
    private lateinit var recipientAdapter: TagPeopleListAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag_people)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setupRecyclerView()
        setupButton()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupEditButon() {

    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)

            recipientAdapter = TagPeopleListAdaptor(InvoiceManager.recipientList)
            recipientAdapter.onTapEdit = {
                editRecipient(it)
            }
            recycler_view.let {
                adapter = recipientAdapter
            }
        }
    }

    private fun editRecipient(position: Int) {
        val intent = Intent(this, AddRecepientForm::class.java)
        Log.d("editPosition",position.toString())
        intent.putExtra("editId",position)
        startActivityForResult(intent, GO_TO_ADD_RECIPIENT)
    }

    private fun setupButton() {
        button_add_tag_people.setOnClickListener {
            val intent = Intent(this, AddRecepientForm::class.java)
            startActivityForResult(intent, GO_TO_ADD_RECIPIENT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == GO_TO_ADD_RECIPIENT) {
            setupRecyclerView()
        }
    }

    companion object {
        private const val GO_TO_ADD_RECIPIENT = 1005
    }
}
