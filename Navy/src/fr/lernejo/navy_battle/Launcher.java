package fr.lernejo.navy_battle;

import fr.hyper.http.NavyServer;

public class Launcher {

	public static void main(String[] args) {
		if(args.length < 1) {
			System.out.println("Port needed !");
			return;
		}
		new NavyServer(Integer.valueOf(args[0])).run();
	}

}
