package you;

import clowns.Clown;
import clowns.Volkswagen;

import java.util.ArrayList;
import java.util.List;

public class You {
    public static void main(String args[]) {
        // put 20 clowns into a Volkswagen
        final Volkswagen vw = new Volkswagen();
        final List<Thread> jammers = new ArrayList<Thread>(20);

        // this will jam 20 clowns into the car, but we're still not done as size is not 20
        for (int i = 0; i < 20; i++) {
            final Thread jammer = new Jammer(vw);
            jammer.start();
            jammers.add(jammer);
        }

        // each clown is now in a state to be added to the set, and bypassed the protection
        synchronized (vw) {
            vw.notify();
        }
        // wait for each jammer to complete it's work
        for (Thread jammer : jammers) {
            try {
                jammer.join();
            }
            catch (InterruptedException e) {
                // meh
            }
        }

        // we got 20
        vw.done();
    }

    public static class Jammer extends Thread {
        private Volkswagen car;

        public Jammer(Volkswagen car) {
            this.car = car;
        }

        @Override
        public void run() {
            car.add(new TrippyClown(car));
        }
    }

    // the trippy clown is a fun little feller. He is basically just like a regular clown
    // but due to his addiction to halucinagenics and pain killers he responds to questions like
    // what's your hashCode really slowly.
    // Ok that's not true, he's really in cahuts with the other clowns when answering questions...
    public static class TrippyClown extends Clown {
        private Volkswagen car;

        public TrippyClown(Volkswagen car) {
            this.car = car;
        }

        @Override
        public int hashCode() {
            // you see... the hash will not add an element to itself unless this method returns
            // so the goal is to return from this method from all clowns simultaneously... or at least when we
            // hit the very end.
            try {
                // wait until someone gets the ball rolling on getting objects added to the set
                car.wait();
                // notify the next (keep the ball rolling)
                car.notify();
            }
            catch (InterruptedException e) {
                //meh
            }
            return super.hashCode();
        }
    }
}