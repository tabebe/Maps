jQuery(document).ready(function($) {
	$("#textbox").bind('keyup', function(event) {	
		var string = $("#textbox").val();
		string = JSON.stringify(string);
		var postParameters = {string: string};
		$.post("/results", postParameters, function(responseJSON) {
			responseObject = JSON.parse(responseJSON);
			var list = responseObject.matches;
			if (list[0] != null) {
				$("#1").html(list[0]);
			}
			if (list[1] != null) {
				$("#2").html(list[1]);
			}
			if (list[2] != null) {
				$("#3").html(list[2]);
			}
			if (list[3] != null) {
				$("#4").html(list[3]);
			}
			if (list[4] != null) {
				$("#5").html(list[4]);
			}
		})
	})
	
});