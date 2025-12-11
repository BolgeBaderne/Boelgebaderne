package com.example.bolgebaderne.dto;

public class WaitlistStatusDTO {

        private boolean fullyBooked;
        private int waitlistCount;

        public WaitlistStatusDTO(boolean fullyBooked, int waitlistCount)
        {
            this.fullyBooked = fullyBooked;
            this.waitlistCount = waitlistCount;
        }

        public boolean isFullyBooked()
        {
            return fullyBooked;
        }

        public int getWaitlistCount()
        {
            return waitlistCount;
        }
}

