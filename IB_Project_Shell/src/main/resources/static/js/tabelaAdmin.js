$(document).ready(function() {
	$("#logout").click(function(event){

		window.location.replace='login.html';
		
		localStorage.removeItem(localStorage.getItem("access_token"));
		  
	});

	
	var adminTabela = $('#adminTabela'); 
	
	$.ajax({
        url: "https://localhost:8443/auth/user/loadAllUnenabled", 
        type:"GET",
        data : 'json',
	    contentType: "application/json; charset=UTF-8"

         ,success: function(res){
        	 console.log(res);
            var index = 0;
            var subObj = '';
            var htm = '';
            adminTabela.append('<tbody>');
            for(index = 0; index < res.length; index++) {
            	subObj    =   res[index];
            	adminTabela.append(
						'<tr class=\'clickable-row\' data-href=\'url://\'>' + 
							
							'<td>' + subObj.username + '</td>' + 
							'<td>' + subObj.email + '</td>' + 
							'<td>' + subObj.enabled + '</td>' + 
							'<td><button id="odobri">Odobri</button></td>' + 
						'</tr>'
					)
					$("#odobri").on('click',function(event) {
		            	console.log(subObj.id)
		            	$.ajax({
						        url: "https://localhost:8443/auth/enable/"+subObj.id, 
						        type:"GET",
						        data : 'json',
							    contentType: "application/json; charset=UTF-8"
						
						         ,success: function(res){
						        	 window.location.replace='tabelaAdmin.html';
		            
					},error:function(resp){


			          }
		          });
		            	
		            	
		            });

          }
            adminTabela.append('</tbody>');
            


         },error:function(resp){


          }

    });
				

});