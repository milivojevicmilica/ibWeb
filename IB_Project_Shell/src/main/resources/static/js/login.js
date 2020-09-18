$(document).ready(function() {
	

	  $("#login").click(function(event){
		  
		  var user = $("#lg_username").val();
		  var pass = $("#lg_password").val();

		  var loginForm = {
			'username': user,
			'password': pass
		  };
		  
		  console.log(loginForm);
		  
		  var xhr = new XMLHttpRequest();		  
		  xhr.open("POST", "https://localhost:8443/auth/login", true);
		  console.log(xhr);
		  xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
		  xhr.responseType = 'json';
		  xhr.onreadystatechange = function() {
		        if (xhr.readyState === 4 && xhr.status == 200) {
		            console.log(this.response["access_token"]);
		            localStorage.setItem('access_token', this.response["access_token"]);
		            var decoded = jwt_decode(localStorage.getItem("access_token"));
		            alert(decoded.ToString());
		            //console.log(localStorage.getItem("access_token"));
//		            window.location();
		            console.log(decoded["sub"]);
		            alert(decoded["sub"]);
		            if(decoded["sub"] !="user"){
		            	
		            	window.location.replace='upload.html';
		            }
		            else{
		            	window.location.replace='tabelaAdmin.html';
		            }
		       }
		    };
		    
		  xhr.send(JSON.stringify(loginForm));
		  
//		  $.ajax({
//			    url : "/auth/login",
//			    type: "POST",
//			    data : JSON.stringify(loginForm),
//			    contentType: "application/json; charset=UTF-8",
//			    success: function(data)
//			    {
//			        console.log(data);
//			    }
//			});
		  
	  });
	  
	  $("#register").click(function(event){
		  
		  var user = $("#reg_username").val();
		  var pass = $("#reg_password").val();
		  var last=$("#reg_lastfullname").val();
		  var name=$("#reg_fullname").val();
		  var email=$("#reg_email").val();
		  

		  var registerForm = {
			'firstname':name,
			'lastname':last,
			'username': user,
			'password': pass,
			'email':email
		  };
		  
		  console.log(registerForm);
		  
		  var xhr = new XMLHttpRequest();		  
		  xhr.open("POST", "https://localhost:8443/auth/register", true);
		  xhr.setRequestHeader("Content-Type", "application/json;charset=utf-8");
		  xhr.responseType = 'json';

		  xhr.send(JSON.stringify(registerForm));
		  window.location.replace='login.html';

		  
	  });
	
});