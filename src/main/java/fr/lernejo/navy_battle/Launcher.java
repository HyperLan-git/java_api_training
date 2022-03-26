package fr.lernejo.navy_battle;

import fr.hyper.http.NavyClient;
import fr.hyper.http.NavyServer;

public class Launcher {
	public static void main(String[] args) {
		System.out.println(args.length);
		if(args.length < 1) {
			System.out.println("Port needed !");
			return;
		}
		if (args.length > 1) {
			Thread t2 = new Thread(new NavyClient(Integer.valueOf(args[0]), args[1]));
			t2.start();
			while(t2.isAlive())
				;
			return;
		}
		Thread t = new Thread(new NavyServer(Integer.valueOf(args[0])));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.print(e);
		}
	}
}
