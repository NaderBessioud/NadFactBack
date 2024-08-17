const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/gs-guide-websocket'
});

stompClient.onConnect = (frame) => {
   
    console.log('Connected: '+frame);
    stompClient.subscribe('/topic/messages', (notifications) => {
        if(id==(JSON.parse(notifications.body)).sender.idU ||
          id==(JSON.parse(notifications.body)).receiver.idU){
			  let newMessage = { content:(JSON.parse(notifications.body)).content,date:(JSON.parse(notifications.body)).date,
			  hour:(JSON.parse(notifications.body)).hour,sender:(JSON.parse(notifications.body)).sender,receiver:(JSON.parse(notifications.body)).receiver,
			  day:"Autjourd'hui" };
			  window.message.push(newMessage)
			  
			  }
    });
};