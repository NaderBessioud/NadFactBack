
import HelloSign from "hellosign-embedded";


    export function embededsigning(id){
	fetch('/ERPPro/facturationsign/hellosign/embeddedsign/'+id, {
         method: 'POST',
       
     })
     .then(response => response.text())
  		.then(data => {
    	console.log(data);
    	var client = new HelloSign({
	  clientId: "683073ea061bdf3d01bb69f28d5d9296"
	});

	client.open(text, {
	  hideHeader: true,
	  testMode: true
	});
})

	
	}