package com.massttr.user

import java.io.Serializable

/**
 * created by Nikul on 18/12/20
 */
object DataProvider {
//    fun sliderList(): ArrayList<SliderData> {
//        val list = arrayListOf<SliderData>()
//        list.add(
//            SliderData(
//                R.drawable.intro_vector,
//                "Find and Mine Crypto Near You",
//                "Instant, Secure, Private"
//            )
//        )
//        list.add(
//            SliderData(
//                R.drawable.intro_vector_2,
//                "Find Associate for your mining machine",
//                "Get Huge Return"
//            )
//        )
//        list.add(
//            SliderData(
//                R.drawable.intro_vector_3,
//                "Find Miner for your investment",
//                "Get Huge Return"
//            )
//        )
//        return list
//    }

//    fun notificationList(): ArrayList<Notification> {
//        val list = arrayListOf<Notification>()
//        list.add(Notification("Associate amount", "your associate is pending", "10 Dec 20"))
//        list.add(Notification("Associate amount", "your associate is pending", "10 Dec 20"))
//        list.add(Notification("Associate amount", "your associate is pending", "10 Dec 20"))
//        list.add(Notification("Associate amount", "your associate is pending", "10 Dec 20"))
//        return list
//    }

//    fun ticketList(): ArrayList<TicketList> {
//        val list = arrayListOf<TicketList>()
//        list.add(
//            TicketList(
//                "General Query",
//                "This is subject testing",
//                "it is long established fact that reader will be distracted by the readable content.",
//                "10 Dec 20",
//                0
//            )
//        )
//        list.add(
//            TicketList(
//                "General Query",
//                "This is subject testing",
//                "it is long established fact that reader will be distracted by the readable content.",
//                "10 Dec 20",
//                1
//            )
//        )
//        list.add(
//            TicketList(
//                "General Query",
//                "This is subject testing",
//                "it is long established fact that reader will be distracted by the readable content.",
//                "10 Dec 20",
//                0
//            )
//        )
//        return list
//    }

//    fun supportCenter(): ArrayList<SupportCenter> {
//        val list = arrayListOf<SupportCenter>()
//        list.add(SupportCenter("Rose fields", "Thank you quick replay", "10 Dec 20"))
//        list.add(SupportCenter("Rose fields", "Thank you quick replay", "10 Dec 20"))
//        list.add(SupportCenter("Rose fields", "Thank you quick replay", "10 Dec 20"))
//        return list
//    }
//
//    fun chatList(): ArrayList<ChatMessage> {
//        val list = arrayListOf<ChatMessage>()
//        list.add(ChatMessage("Hello, Dear How can i help you?", 1))
//        list.add(ChatMessage("I need your help, to solve..", 0))
//        return list
//    }


}

data class Product(
    val title: String = "",
    val price: String = "",
    val type: String = "",
    var state: Boolean = false

)

fun productList(): ArrayList<Product> {
    val list = arrayListOf<Product>()
    list.add(Product("Trade 3 Speed Code Drill", "$436.10", "Machinery", true))
    list.add(Product("Dust Shroud for 5 inch Grinder", "$4510.2", "Machinery", true))
    list.add(Product("Dust Shroud for 5 inch Bosch Grinder", "$121.00", "Tiling", false))
    return list
}

data class Sizes(
    val size: String = "",
    var status: Boolean = false
)

fun sizeList(): ArrayList<Sizes> {
    val list = arrayListOf<Sizes>()
    list.add(Sizes("10 MM PRO NOTCH TROWEL", true))
    list.add(Sizes("10 MM PRO NOTCH TROWEL", true))
    list.add(Sizes("10 MM PRO NOTCH TROWEL", true))
    return list
}

data class CategoryProduct(
    val category: String = "",
    var isSelected: Boolean = false
)

fun categoryList(): ArrayList<CategoryProduct> {
    val list = arrayListOf<CategoryProduct>()
    list.add(CategoryProduct("Machinery", true))
    list.add(CategoryProduct("Tiling", false))
    list.add(CategoryProduct("Concrete Tools", false))
    list.add(CategoryProduct("Machinery", false))
    return list
}

data class FixPhoto(
    val photo: String = "",
) : Serializable

fun fixerPhoto(): ArrayList<FixPhoto> {
    val list = arrayListOf<FixPhoto>()
    list.add(FixPhoto("https://images.unsplash.com/photo-1630765825457-25811a64113f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1833&q=80"))
    list.add(FixPhoto("https://images.unsplash.com/photo-1630765825457-25811a64113f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1833&q=80"))
    list.add(FixPhoto("https://images.unsplash.com/photo-1630765825457-25811a64113f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1833&q=80"))
    list.add(FixPhoto("https://images.unsplash.com/photo-1630765825457-25811a64113f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1833&q=80"))
    list.add(FixPhoto("https://images.unsplash.com/photo-1630765825457-25811a64113f?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=1833&q=80"))
    return list
}

data class FixerNotification(
    val photo: String = "",
    val task: String = "",
    val description: String = "",
)

fun fixerNoti(): ArrayList<FixerNotification> {
    val list = arrayListOf<FixerNotification>()

    list.add(
        FixerNotification(
            "",
            "Task has been\n Successfully completed",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."
        )
    )
    list.add(
        FixerNotification(
            "",
            "Task has been\n Successfully completed",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."
        )
    )
    list.add(
        FixerNotification(
            "",
            "Task has been\n Successfully completed",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."
        )
    )
    list.add(
        FixerNotification(
            "",
            "Task has been\n Successfully completed",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit..."
        )
    )

    return list
}

data class SubCategory(
    val id: Int? = 0,
    val subCategoryName: String,
    var isSelect: Boolean
) : Serializable

fun subCategory(): ArrayList<SubCategory> {
    val list = arrayListOf<SubCategory>()
    list.add(SubCategory(0, "Help Moving", false))
    list.add(SubCategory(0, "Moving small/big items", false))
    list.add(SubCategory(0, "Furniture Movers", false))
    list.add(SubCategory(0, "Furniture Removal ", false))
    list.add(SubCategory(0, "Remove heavy furniture ", false))
    return list
}

data class Category(
    val category: String
)

fun category(): ArrayList<Category> {
    val list = arrayListOf<Category>()

    list.add(Category("Help Moving"))
    list.add(Category("Furniture Movers"))
    return list
}

data class FixerList(
    val photo: String = "",
    val name: String = "",
    val description: String = "",
    val km: String = "",
    val workName: String = "",

    val equipment: String = ""
)

fun fixerList(): ArrayList<FixerList> {
    val list = arrayListOf<FixerList>()
    list.add(
        FixerList(
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fuser&psig=AOvVaw0bfnFs7p_02Iq0BffFqRbu&ust=1631183150590000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJjfqviU7_ICFQAAAAAdAAAAABAI",
            "Jone",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
            "0.2 km",
            "I need a plumber",
            "Grill machine"
        )
    )
    list.add(
        FixerList(
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fuser&psig=AOvVaw0bfnFs7p_02Iq0BffFqRbu&ust=1631183150590000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJjfqviU7_ICFQAAAAAdAAAAABAI",
            "Adam",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
            "0.6 km",
            "I need a plumber",
            "pvc pipe"
        )
    )
    list.add(
        FixerList(
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fuser&psig=AOvVaw0bfnFs7p_02Iq0BffFqRbu&ust=1631183150590000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJjfqviU7_ICFQAAAAAdAAAAABAI",
            "Addison",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
            "0.8 km",
            "I need a plumber",
            "pvc pipe"
        )
    )
    list.add(
        FixerList(
            "https://www.google.com/url?sa=i&url=https%3A%2F%2Funsplash.com%2Fs%2Fphotos%2Fuser&psig=AOvVaw0bfnFs7p_02Iq0BffFqRbu&ust=1631183150590000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJjfqviU7_ICFQAAAAAdAAAAABAI",
            "Adel",
            "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
            "0.0 km",
            "I need a plumber",
            "pvc pipe"
        )
    )
    return list
}

data class DelayOrder(
    val delay: Int,
    var isSelected: Boolean = false
)

fun getDelayList(): ArrayList<DelayOrder> {
    val list = arrayListOf<DelayOrder>()
    list.add(DelayOrder(10, true))
    list.add(DelayOrder(30, false))
    list.add(DelayOrder(45, false))
    list.add(DelayOrder(60, false))
    return list
}
//data class TicketList(
//    val title: String = "",
//    val subTitle: String = "",
//    val description: String = "",
//    val date: String = "",
//    val status: Int = 0,
//) : Serializable

//data class Notification(
//    val title: String = "",
//    val SubTitle: String = "",
//    val date: String = ""
//)

//data class SupportCenter(
//    val ticket_title: String = "",
//    val ticket_SubTitle: String = "",
//    val ticket_date: String = ""
//)
//
//data class ChatMessage(
//    val message: String = "",
//    val type: Int = 0, //sender
//) : Serializable

