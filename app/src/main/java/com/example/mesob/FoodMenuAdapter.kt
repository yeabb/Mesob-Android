package com.example.mesob

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class FoodMenuAdapter(private var foodMenuArr: ArrayList<FoodMenuData>):

    RecyclerView.Adapter<FoodMenuAdapter.FoodMenuViewHolder>() {

    var onItemClickListener: ((FoodMenuData) -> Unit)? = null

    inner class FoodMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val siFoodImage: ShapeableImageView = itemView.findViewById(R.id.siFoodImage)
        val tvFoodName: TextView = itemView.findViewById(R.id.tvFoodName)
        val tvRestaurantName: TextView = itemView.findViewById(R.id.tvRestaurantName)
        val tvFoodCreditNumber: TextView = itemView.findViewById(R.id.tvFoodCreditNumber)
        val star1: ImageView = itemView.findViewById(R.id.star1)
        val star2: ImageView = itemView.findViewById(R.id.star2)
        val star3: ImageView = itemView.findViewById(R.id.star3)
        val star4: ImageView = itemView.findViewById(R.id.star4)
        val star5: ImageView = itemView.findViewById(R.id.star5)

        val starImageViews = listOf(star1, star2, star3, star4, star5)

        fun bind(foodMenu: FoodMenuData) {
            siFoodImage.setImageResource(R.drawable.kitfo)
            tvFoodName.text = foodMenu.foodName
            tvRestaurantName.text = foodMenu.restaurantName

            // Set the queue status text based on the data
            val foodCreditNumber = foodMenu.foodCreditNumber
            tvFoodCreditNumber.text = foodCreditNumber.toString()


            for (i in 0..4) {


                if (i < foodMenu.rating) {
                    starImageViews[i].setImageResource(R.drawable.ic_filled_star)
                } else {
                    starImageViews[i].setImageResource(R.drawable.ic_empty_star)
                }
            }



            itemView.setOnClickListener {
                onItemClickListener?.invoke(foodMenu)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodMenuViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_foodmenu, parent, false)
        return FoodMenuViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return foodMenuArr.size
    }

    override fun onBindViewHolder(holder: FoodMenuViewHolder, position: Int) {
        val currentItem = foodMenuArr[position]
        holder.siFoodImage.setImageResource(R.drawable.kitfo)
        holder.tvFoodName.text = currentItem.foodName
        holder.tvRestaurantName.text = currentItem.restaurantName

        // Set the queue status text based on the data
        val foodCreditNumber = currentItem.foodCreditNumber
        holder.tvFoodCreditNumber.text = foodCreditNumber.toString()

        holder.bind(currentItem)
    }
}