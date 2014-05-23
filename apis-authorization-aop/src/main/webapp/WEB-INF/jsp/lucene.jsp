<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">   
<head> 
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
  <title>ACEONA OpenPlatform</title>
  <script src="/js/jquery-1.4.4.min.js"></script>
  <script>
  $(document).ready(function(){
  	$("#single-thread-rebuild").click(function(){
  		alert(1);
  		$.ajax({
		  url: 'lucene.php',
		  success: function(data) {
		    //$('.result').html(data);
		    alert('Load was performed.');
		  }
		});
  	});
  });
  </script>
</head>
  
<body>
<div style="margin:20px 20px 20px 0px;border-bottom:2px solid">
<input type="button" value="Rebuild Index" id="single-thread-rebuild"/>
<input type="button" value="Multi Thread Index" id="multi-thread-rebuild"/>

</div>
<div>
<label for="start_id">start_Id:</label><input id="start_id" type="text"/><br/><br/>
<label for="end_Id">&nbsp;&nbsp;end_Id:</label><input id="end_id" type="text"/>
</div>
<div style="text-align:left;border-bottom:2px solid;"><input type="button" value="build" id="build"/>
</div>
<div style="text-align:left;border-bottom:2px solid;"><input type="button" value="optimize" id="optimize"/>
</div>

Console:
<div style="border:1px red solid;height:50px;overflow:auto;">
</div>
</body>
</html>
