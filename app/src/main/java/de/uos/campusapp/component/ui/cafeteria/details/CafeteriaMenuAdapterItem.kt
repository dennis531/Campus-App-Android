package de.uos.campusapp.component.ui.cafeteria.details

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.uos.campusapp.R
import de.uos.campusapp.component.ui.cafeteria.FavoriteDishDao
import de.uos.campusapp.component.ui.cafeteria.model.database.CafeteriaMenuItem
import de.uos.campusapp.component.ui.cafeteria.model.database.FavoriteDish
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

sealed class CafeteriaMenuAdapterItem {

    abstract val id: String

    abstract fun bind(
        holder: CafeteriaMenusAdapter.ViewHolder,
        listener: (() -> Unit)?
    )

    data class Header(val menu: CafeteriaMenuItem) : CafeteriaMenuAdapterItem() {

        override val id: String
            get() = "header_${menu.id}"

        override fun bind(
            holder: CafeteriaMenusAdapter.ViewHolder,
            listener: (() -> Unit)?
        ) = with(holder.itemView) {
            val headerTextView = findViewById<TextView>(R.id.headerTextView)

            headerTextView.text = menu.type
            setOnClickListener { listener?.invoke() }
        }
    }

    data class Item(
        val menu: CafeteriaMenuItem,
        val isFavorite: Boolean = false,
        val isBigLayout: Boolean,
        val favoriteDishDao: FavoriteDishDao
    ) : CafeteriaMenuAdapterItem() {

        override val id: String
            get() = "item_${menu.id}"

        override fun bind(
            holder: CafeteriaMenusAdapter.ViewHolder,
            listener: (() -> Unit)?
        ) = with(holder.itemView) {
            val nameTextView = findViewById<TextView>(R.id.nameTextView)

            nameTextView.text = if (isBigLayout) {
                menu.name
            } else {
                // Remove all parentheses from the menu
                menu.name.replace("\\(.*?\\)".toRegex(), "").trim()
            }

            setOnClickListener { listener?.invoke() }

            val price = menu.getPriceText(context)
            if (price.isNotEmpty()) {
                showPrice(this, price)
            } else {
                hidePrice(this)
            }
        }

        private fun showPrice(
            itemView: View,
            price: String
        ) = with(itemView) {
            val priceTextView = findViewById<TextView>(R.id.priceTextView)
            val favoriteDish = findViewById<ImageView>(R.id.favoriteDish)

            priceTextView.text = price

            favoriteDish.isSelected = isFavorite
            favoriteDish.setOnClickListener { view ->
                if (!view.isSelected) {
                    val formatter = DateTimeFormat.forPattern("dd-MM-yyyy")
                    val date = formatter.print(DateTime.now())
                    favoriteDishDao.insertFavouriteDish(FavoriteDish.create(menu, date))
                    view.isSelected = true
                } else {
                    favoriteDishDao.deleteFavoriteDish(menu.cafeteriaId, menu.name)
                    view.isSelected = false
                }
            }
        }

        private fun hidePrice(itemView: View) = with(itemView) {
            val priceTextView = findViewById<TextView>(R.id.priceTextView)
            val favoriteDish = findViewById<ImageView>(R.id.favoriteDish)

            priceTextView.visibility = View.GONE
            favoriteDish.visibility = View.GONE
        }
    }
}
