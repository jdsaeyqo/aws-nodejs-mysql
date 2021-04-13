package com.example.retrofit_php.model


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.retrofit_php.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserAdapter(private val context: Context, private val userList:MutableList<DataModel.OtherData>,val itemClick : (DataModel.OtherData) -> Unit ):
    RecyclerView.Adapter<UserAdapter.Holder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val view : View = LayoutInflater.from(context).inflate(R.layout.item_userinfo,parent,false)
        return Holder(view)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {


        holder.userNickname.text = userList[position].nickname

        userList[position].email.let {
            FirebaseFirestore.getInstance().collection("profileImages").document(
                it
            ).addSnapshotListener { value, error ->
                if (value == null) {

                    return@addSnapshotListener
                }

                if (value.data != null) {

                    val url = value.data!!["imageUri"]
                    if(url != null){
                        Glide.with(context.applicationContext).load(url).apply(RequestOptions().circleCrop())
                            .into(holder.userImage)
                    }else{
                        holder.userImage.setImageResource(R.drawable.ic_person)
                    }





                }
            }
        }
        holder.bind(userList[position])

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userImage: ImageView = itemView.findViewById(R.id.userImage)
        val userNickname: TextView = itemView.findViewById(R.id.userNickname)

        fun bind(ohterdata : DataModel.OtherData){
            itemView.setOnClickListener { itemClick(ohterdata) }
        }
    }
}