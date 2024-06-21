package com.example.demo.auction;

import com.example.demo.bid.BidDto;
import com.example.demo.bid.BidService;
import com.example.demo.chat.domain.ChatRoom;
import com.example.demo.chat.service.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

@Component
@Slf4j
public class AuctionScheduler {

	@Autowired
	private AuctionService service;
	@Autowired
	private BidService bidService;
	@Autowired
	private ChatRoomService chatRoomService;

	@Scheduled(cron = "0 0/5 * * * *") // 매 5분에 실행
	public void setStatus() {
		Date date =new Date();
		ArrayList<AuctionDto> list=service.getByStatus("경매중");
		for(AuctionDto auction:list) {
			if(auction.getEnd_time().before(date)) {
				auction.setStatus("경매 마감");
				String seller = auction.getSeller().getId();
				BidDto byBuyer = bidService.getByBuyer(auction.getNum());
				log.debug("byBuyer:{}",byBuyer);
				Set<Object> byName = chatRoomService.findByName(byBuyer.getBuyer().getId());
				if (byName.isEmpty()){
					log.debug("byName:{}",byName);
					log.debug("id={}", byBuyer.getBuyer().getId());
					chatRoomService.createChatRoom(String.valueOf(auction.getNum()),byBuyer.getBuyer().getId(), seller);
					return;
				}
				service.save(auction);
				for (Object obj : byName) {
					if (obj instanceof ChatRoom) {
						ChatRoom chatRoom = (ChatRoom) obj;
						String chatRoomSeller = chatRoom.getSeller();
						if (!chatRoomSeller.equals(seller)){
							chatRoomService.createChatRoom(String.valueOf(auction.getNum()),byBuyer.getBuyer().getId(), seller);// Get seller from ChatRoom
						}
						System.out.println("ChatRoom Seller: " + chatRoomSeller);
						// Additional processing if needed
					}
				}
			}
		}
	}
}
