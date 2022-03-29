package fr.lernejo.navy_battle;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import fr.hyper.http.NavyClient;
import fr.hyper.http.NavyServer;

public class Launcher {
	private static void logToFile(String file) {
		File f = new File(file);
		try {
			f.createNewFile();
			System.setOut(new PrintStream(f));
			System.setErr(System.out);
			Thread.sleep(50);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		logToFile("log" + args.length + "_" + System.currentTimeMillis() + ".txt");
		if(args.length < 1) {
			System.out.println("Port needed !");
			return;
		}
		Thread t = null;
		if (args.length > 1) 
			t = new Thread(new NavyClient(Integer.valueOf(args[0]), args[1]));
		else
			t = new Thread(new NavyServer(Integer.valueOf(args[0])));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {
			System.err.print(e);
		}
	}
}
