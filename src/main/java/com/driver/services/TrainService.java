package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train = new Train();
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        List<Station> list = trainEntryDto.getStationRoute();
        String route = "";

        for(int i=0;i<list.size();i++){
            if(i==list.size()-1)
                route += list.get(i);
            else
                route += list.get(i) + ",";
        }
        train.setRoute(route);

        train.setDepartureTime(trainEntryDto.getDepartureTime());
        return trainRepository.save(train).getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //and 2 tickets are booked from A to B and C - D
        //if there is only 1 seat available in total
        //then that seat is there from B -C only and not from any other station
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.
//       Train train = trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
//       List<Ticket> list = train.getBookedTickets();
//
//       int NoOfSeats = 0;
//       if(train.getNoOfSeats()<=list.size())
//           return NoOfSeats;
//
//       int noOfAvailableSeat = train.getNoOfSeats() - list.size();
//       for(Ticket ticket:list){
//           if(ticket.getFromStation()!=seatAvailabilityEntryDto.getFromStation() || ticket.getFromStation()!=seatAvailabilityEntryDto.getToStation())
//       }
       return null;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.

        Train train = trainRepository.findById(trainId).get();
        int totalPeople = 0;
        List<Ticket> list = train.getBookedTickets();

        for(Ticket ticket:list){
            if(ticket.getFromStation()==station)
                totalPeople += ticket.getPassengersList().size();
        }

        if(totalPeople==0)
            throw new Exception("Train is not passing from this station");
        else
            return totalPeople;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        int oldestPerson = 0;
        Train train = trainRepository.findById(trainId).get();
        List<Ticket> list = train.getBookedTickets();
        for(Ticket ticket:list){
            List<Passenger> l = ticket.getPassengersList();
            for(Passenger p:l){
                if(p.getAge()>=oldestPerson)
                    oldestPerson = p.getAge();
            }
        }

        return oldestPerson;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.
        List<Integer> TrainList = new ArrayList<>();
        List<Train> trains = trainRepository.findAll();
        for(Train t:trains){
            String s = t.getRoute();
            String[] ans = s.split(",");
            for(int i=0;i<ans.length;i++){
                if(Objects.equals(ans[i], String.valueOf(station))){
                    int startTimeInMin = (startTime.getHour() * 60) + startTime.getMinute();
                    int lastTimeInMin = (endTime.getHour() * 60) + endTime.getMinute();


                    int departureTimeInMin = (t.getDepartureTime().getHour() * 60) + t.getDepartureTime().getMinute();
                    int reachingTimeInMin  = departureTimeInMin + (i * 60);
                    if(reachingTimeInMin>=startTimeInMin && reachingTimeInMin<=lastTimeInMin)
                        TrainList.add(t.getTrainId());
                }
            }
        }
        return TrainList;
    }

}
