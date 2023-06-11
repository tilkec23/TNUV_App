package si.uni_lj.fe.tnuv.umami_burger

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class ContactsJsonParser {
    private val contactList = ArrayList<HashMap<String, String>>()
    fun parseToArrayList(jsonStr: String?): ArrayList<HashMap<String, String>> {
        try {
            val jsonObj = JSONObject(jsonStr)

            // Getting JSON Array node
            val contacts = jsonObj.getJSONArray("contacts")

            // looping through All Contacts
            for (i in 0 until contacts.length()) {
                val c = contacts.getJSONObject(i)
                val id = c.getString("id")
                val name = c.getString("name")
                val email = c.getString("email")
                val address = c.getString("address")
                val gender = c.getString("gender")

                // Phone node is JSON Object
                val phone = c.getJSONObject("phone")
                val mobile = phone.getString("mobile")
                val home = phone.getString("home")
                val office = phone.getString("office")

                // tmp hash map for single contact
                val contact = HashMap<String, String>()

                // adding each child node to HashMap key => value
                contact["id"] = id
                contact["name"] = name
                contact["email"] = email
                contact["mobile"] = mobile

                // adding contact to contact list
                contactList.add(contact)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Json parsing error: " + e.message)
        }
        return contactList
    }

    companion object {
        private val TAG = ContactsJsonParser::class.java.simpleName
    }
}