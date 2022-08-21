package de.uos.campusapp.component.tumui.person

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import de.uos.campusapp.R
import de.uos.campusapp.api.tumonline.CacheControl
import de.uos.campusapp.component.other.generic.fragment.BaseFragment
import de.uos.campusapp.component.tumui.person.adapteritems.*
import de.uos.campusapp.component.tumui.person.api.PersonAPI
import de.uos.campusapp.component.tumui.person.model.PersonInterface
import de.uos.campusapp.databinding.FragmentPersonDetailsBinding
import de.uos.campusapp.di.injector
import de.uos.campusapp.utils.Const
import de.uos.campusapp.utils.ContactsHelper
import org.jetbrains.anko.doAsync
import javax.inject.Inject

class PersonDetailsFragment : BaseFragment<PersonInterface>(
    R.layout.fragment_person_details,
    R.string.contact_information
) {
    @Inject
    lateinit var apiClient: PersonAPI

    private lateinit var personId: String
    private var person: PersonInterface? = null

    private val binding by viewBinding(FragmentPersonDetailsBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        injector.personComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val person = requireActivity().intent.extras?.getSerializable("personObject") as? PersonInterface
        if (person == null) {
            requireActivity().finish()
            return
        }

        personId = person.id

        loadPersonDetails(person.id, CacheControl.USE_CACHE)
    }

    override fun onRefresh() {
        loadPersonDetails(personId, CacheControl.BYPASS_CACHE)
    }

    private fun loadPersonDetails(personId: String, cacheControl: CacheControl) {
        fetch { apiClient.getPersonDetails(personId) }
    }

    override fun onDownloadSuccessful(response: PersonInterface) {
        this.person = response
        displayResult(response)
        requireActivity().invalidateOptionsMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_contact, menu)
        val addToContactsItem = menu.findItem(R.id.action_add_contact)
        addToContactsItem.isVisible = (person != null)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_contact -> {
                displayAddContactDialog(person)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun displayAddContactDialog(person: PersonInterface?) {
        if (person == null) {
            return
        }

        AlertDialog.Builder(requireContext())
            .setMessage(R.string.dialog_add_to_contacts)
            .setPositiveButton(R.string.add) { _, _ -> addContact(person) }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * Displays all relevant information about the given person in the user
     * interface (UI).
     *
     * @param person The person whose information should be displayed.
     */
    private fun displayResult(person: PersonInterface) {
        binding.scrollView.visibility = View.VISIBLE

        // Set up profile picture
        if (person.imageUrl.isNotBlank()) {
            Picasso.get()
                .load(person.imageUrl)
                .error(R.drawable.photo_not_available)
                .placeholder(R.drawable.photo_not_available)
                .into(binding.pictureImageView)
        } else {
            val image = BitmapFactory.decodeResource(
                resources, R.drawable.photo_not_available)
            binding.pictureImageView.setImageBitmap(image)
        }

        binding.nameTextView.text = person.fullName

        // Set up person institutes
        val institutes = person.institutes
        if (institutes?.isNotEmpty() == true) {
            binding.groupsRecyclerView.setHasFixedSize(true)
            binding.groupsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.groupsRecyclerView.adapter = PersonGroupsAdapter(institutes)
        } else {
            binding.dividerNameGroups.visibility = View.GONE
            binding.groupsRecyclerView.visibility = View.GONE
        }

        // Setup contact items
        val contactItems = arrayListOf<AbstractContactItem>().apply {
            if (person.email.isNotBlank()) {
                add(EmailContactItem(person.email))
            }

            person.homepage.let { homepage ->
                if (homepage.isNotBlank()) {
                    add(HomepageContactItem(homepage))
                }
            }
        }

        person.phoneNumbers?.forEach { phone ->
            if (phone.isNotBlank()) {
                contactItems.add(PhoneContactItem(phone))
            }
        }

        if (person.mobilephone.isNotBlank()) {
            contactItems.add(MobilePhoneContactItem(person.mobilephone))
        }

        if (person.additionalInfo.isNotBlank()) {
            contactItems.add(InformationContactItem(person.additionalInfo))
        }

        if (person.consultationHours.isNotBlank()) {
            contactItems.add(OfficeHoursContactItem(person.consultationHours))
        }

        person.rooms?.let { rooms ->
            rooms.forEach { room ->
                if (room.getFullLocation().isNotBlank()) {
                    contactItems.add(RoomContactItem(room.getFullLocation(), room.getQueryString()))
                }
            }
        }

        if (contactItems.isNotEmpty()) {
            binding.contactItemsRecyclerView.setHasFixedSize(true)
            binding.contactItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.contactItemsRecyclerView.adapter = PersonContactItemsAdapter(contactItems)
        } else {
            binding.dividerGroupsContactItems.visibility = View.GONE
            binding.contactItemsRecyclerView.visibility = View.GONE
        }
    }

    /**
     * Adds the given person to the users contact list
     *
     * @param person Object to insert into contacts
     */
    private fun addContact(person: PersonInterface?) {
        if (!isPermissionGranted(Const.CONTACTS_PERMISSION_REQUEST_CODE)) {
            return
        }

        if (person != null) {
            val imageBitmap = binding.pictureImageView.drawable?.toBitmap()
            doAsync {
                ContactsHelper.saveToContacts(this@PersonDetailsFragment.requireContext(), person, imageBitmap)
            }
        }
    }

    /**
     * Check Contacts permission for Android 6.0
     *
     * @param id the request id
     * @return If the contacts permission was granted
     */
    private fun isPermissionGranted(id: Int): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        // Provide an additional rationale to the user if the permission was not granted
        // and the user would benefit from additional context for the use of the permission.
        // For example, if the request has been denied previously.
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_CONTACTS)) {
            // Display an AlertDialog with an explanation and a button to trigger the request.
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.permission_contacts_explanation)
                .setPositiveButton(R.string.grant_permission) { _, _ ->
                    ActivityCompat.requestPermissions(this@PersonDetailsFragment.requireActivity(), PERMISSIONS_CONTACTS, id)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS_CONTACTS, id)
        }

        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // Rerun the interrupted action
            addContact(person)
        }
    }

    companion object {
        private val PERMISSIONS_CONTACTS = arrayOf(Manifest.permission.WRITE_CONTACTS)

        fun newInstance() = PersonDetailsFragment()
    }
}