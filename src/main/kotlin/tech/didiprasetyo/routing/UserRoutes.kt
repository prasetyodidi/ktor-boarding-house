package tech.didiprasetyo.routing

import io.ktor.server.routing.*

fun Route.userRouting(){
    route("/user/{userId}"){
        get{
            // get parameter userId
            // get user info
            // return
        }
        post{
            // get parameter userId
            // update user
            // return
        }
    }
}