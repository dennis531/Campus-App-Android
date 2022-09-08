package de.uos.campusapp.utils

import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.graphics.Bitmap
import android.os.RemoteException
import android.provider.ContactsContract
import com.squareup.picasso.Picasso
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.person.model.PersonInterface
import org.jetbrains.anko.runOnUiThread
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ContactsHelper {

    companion object {

        @JvmStatic fun saveToContacts(context: Context, person: PersonInterface, image: Bitmap? = null) {
            val ops = ArrayList<ContentProviderOperation>()

            val rawContactID = ops.size

            // Adding insert operation to operations list
            // to insert a new raw contact in the table ContactsContract.RawContacts
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build())

            // Add full name
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.PREFIX, person.title)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, person.firstName)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, person.lastName)
                    .build())

            // Add e-mail address
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, person.email)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build())

            val phoneNumbers = person.phoneNumbers
            if (phoneNumbers != null) {
                for (number in phoneNumbers) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                            .build())
                }
            }

            // Add work mobile number
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, person.mobilephone)
                .build())
            // Add work fax number
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, person.fax)
                .build())
            // Add website
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Website.URL, person.homepage)
                .build())

            // Add organisations
            val organisationResId = ConfigUtils.getConfig(ConfigConst.ORGANISATION_NAME, R.string.organisation)
            person.institutes?.let { institutes ->
                institutes.forEach { institute ->
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, context.getString(organisationResId))
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, institute.name)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                            .build())
                }
            }

            // Add office hours
            val notes = StringBuilder()
            if (person.consultationHours.isNotBlank()) {
                notes.append(context.getString(R.string.office_hours))
                    .append(": ")
                    .append(person.consultationHours)
            }

            // saveToContacts all rooms
            val rooms = person.rooms
            if (rooms != null && rooms.isNotEmpty() && rooms[0].getFullLocation().isNotBlank()) {
                if (!notes.toString()
                                .isEmpty()) {
                    notes.append('\n')
                }
                notes.append(context.getString(R.string.room))
                        .append(": ")
                        .append(rooms[0].getFullLocation())
            }

            // Finally saveToContacts notes
            if (!notes.toString().isEmpty()) {
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, notes.toString())
                        .build())
            }

            // Add image
            val imageUrl = person.imageUrl
            if (imageUrl.isNotBlank()) { // If an image is selected successfully
                var bitmap = image
                if (bitmap == null) {
                    bitmap = tryOrNull {
                        Picasso.get()
                            .load(person.imageUrl)
                            .get()
                    }
                }

                bitmap?.let {
                    val stream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.PNG, 75, stream)

                    // Adding insert operation to operations list
                    // to insert Photo in the table ContactsContract.Data
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                        .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                        .build())

                    try {
                        stream.flush()
                    } catch (e: IOException) {
                        Utils.log(e)
                    }
                }
            }

            // Executing all the insert operations as a single database transaction
            try {
                context.contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
                context.runOnUiThread {
                    Utils.showToast(context, R.string.contact_added)
                }
            } catch (e: RemoteException) {
                Utils.log(e)
            } catch (e: OperationApplicationException) {
                Utils.log(e)
            }
        }
    }
}