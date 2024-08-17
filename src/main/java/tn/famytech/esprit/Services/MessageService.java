package tn.famytech.esprit.Services;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import tn.famytech.esprit.Entites.Message;
import tn.famytech.esprit.Entites.User;
import tn.famytech.esprit.Repositories.MessageRepo;
import tn.famytech.esprit.Repositories.UserRepository;

@Service
public class MessageService {
	@Autowired
	private MessageRepo messageRepo;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private SimpMessagingTemplate template;
	
	public Message addMessage(String content,long ids,long idr) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		
		Message message=new Message();
		User sender=userRepository.findById(ids).get();
		User receiver=userRepository.findById(idr).get();
		message.setSender(sender);
		message.setReciever(receiver);
		message.setContent(content);
		message.setDate(new Date());
		message.setHour(format.format(new Date()));
		
		
		message.setDay("Aujourd'ui");
	
		
		message=messageRepo.save(message);
		template.convertAndSend("/topic/messages", message);
		return message;
				
		
		
	}
	
	public List<Message> getMessages(long ids,long idr){
		String pattern = "E, dd MMM yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));
		List<Message> result=new ArrayList<>();
		User sender=userRepository.findById(ids).get();
		User reciever=userRepository.findById(idr).get();
		result=messageRepo.findBySenderAndReciever(sender, reciever);
		result.addAll(messageRepo.findBySenderAndReciever(reciever, sender));
		Date date=new Date();
		LocalDate local1=date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		for (Message message : result) {
			
			LocalDate local2=Instant.ofEpochMilli(message.getDate().getTime())
				      .atZone(ZoneId.systemDefault())
				      .toLocalDate();
			
			
			 
	            
	            
			
			
			if(Period.between(local1, local2).getDays()==0) {
				message.setDay("Aujourd'ui");
			}
			else if (Period.between(local1, local2).getDays()==-1) {
				message.setDay("Hier");
			}
			
			else if (Period.between(local1, local2).getDays()==-2) {
				message.setDay("il y a 2 jours");
			}
			else {
				message.setDay("null");
			}
		}
		Collections.sort(result, new Comparator<Message>(){
		    public int compare(Message s1,Message s2) {
		        if(s1.getIdM()>s2.getIdM()) {
		        	return 1;
		        }
		        else if(s1.getIdM()<s2.getIdM()) {
		        	return -1;
		        }
		        else {
		        	return 0;
		        }
		    }
		});
		
		return result;
	}
	
	public void ReadMsg(List<Long> messagesId) {
		for (Long mid : messagesId) {
			Message message=messageRepo.findById(mid).get();
			if(!message.isSeen()) {
				message.setSeen(true);
				messageRepo.save(message);
			}
				
		}
	} 
	
	public boolean ReadSingleMSG( List<Long> ids) {
	boolean existlast=false;
		for (Long id : ids) {
			Message msg=messageRepo.findById(id).get();
			if(getLasMessage(msg.getSender().getIdU(), msg.getReciever().getIdU()).getIdM()==id) {
				existlast=true;
			}
			if(!msg.isSeen()) {
				msg.setSeen(true);
				messageRepo.save(msg);
			}
		}
		return existlast;
		
	}
	
	
	public Message getLasMessage(long ids,long idr) {
		User sender=userRepository.findById(ids).get();
		User receiver=userRepository.findById(idr).get();
		if(messageRepo.getLastMsg(sender, receiver).size() != 0){
			return messageRepo.getLastMsg(sender, receiver).stream().findFirst().get();
		}
		else
		return null;
		
	}
		
	
	
	
	 

}
