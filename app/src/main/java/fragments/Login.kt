package fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation.findNavController
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar
import entities.User

class Login : Fragment() {

    lateinit var v: View
    lateinit var btnLogin: Button
    lateinit var btnRegister: Button
    lateinit var name: EditText
    lateinit var pass: EditText
    var userList: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_login, container, false)

        btnLogin = v.findViewById(R.id.btnNavigate2)
        btnRegister = v.findViewById(R.id.btnNavigate)
        name = v.findViewById(R.id.userName)
        pass = v.findViewById(R.id.userPassword)

        userList.add(User("Martin", "commisso17"))

        return v
    }

    override fun onStart() {
        super.onStart()

        btnLogin.setOnClickListener {

            var name = name.text.toString()
            var enteredPass = pass.text.toString()


            if (userList.isNotEmpty()) {
                val user = userList.find { it.name == name }
                if (user != null) {
                    if (user.password == enteredPass) {
                        val bundle = Bundle()
                        bundle.putString("userName", name)
                        val action = LoginDirections.actionLoginToHome2()
                        findNavController(v).navigate(action)
                    }
                    }
                else {
                    Snackbar.make(v, "Contraseña incorrecta", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        btnRegister.setOnClickListener{
            val action = LoginDirections.actionLoginToRegisterOptions2()
            findNavController(v).navigate(action)
        }
    }
}





