package com.example.mesob

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import org.w3c.dom.Text
import java.util.Date

class ReservationsAdapter(private var foodMenuArr: ArrayList<FoodMenuData>,
    private val foodPickUpDateTimesArr: ArrayList<Date>):

    RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder>() {

    var onItemClickListener: ((FoodMenuData, Date) -> Unit)? = null

    inner class ReservationsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val siFoodImage: ShapeableImageView = itemView.findViewById(R.id.siFoodImage)
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        val tvFoodPickUpDateTime : TextView = itemView.findViewById(R.id.tvFoodPickUpDateTime)




        fun bind(foodMenu: FoodMenuData, foodPickUpDateTime: Date) {
            siFoodImage.setImageResource(R.drawable.kitfo)
            tvFoodName.text = foodMenu.foodName
            tvRestaurantName.text = foodMenu.restaurantName
            tvFoodPickUpDateTime.text = foodPickUpDateTime.toString()

            itemView.setOnClickListener {
                onItemClickListener?.invoke(foodMenu, foodPickUpDateTime)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationsViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_reservation, parent, false)
        return ReservationsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return foodPickUpDateTimesArr.size
    }

    override fun onBindViewHolder(holder: ReservationsViewHolder, position: Int) {
        val currentItem = foodMenuArr[position]
        val foodPickUpDateTime = foodPickUpDateTimesArr[position]
        holder.siFoodImage.setImageResource(R.drawable.kitfo)
        holder.tvFoodName.text = currentItem.foodName
        holder.tvRestaurantName.text = currentItem.restaurantName
        holder.tvFoodPickUpDateTime.text = foodPickUpDateTime.toString()


        holder.bind(currentItem, foodPickUpDateTime)
    }
}
