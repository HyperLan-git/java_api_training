package fr.lernejo.navy_battle;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import fr.hyper.http.NavyClient;
import fr.hyper.http.NavyServer;

public class Launcher {
	public static void main(String[] args) {
		File f = new File("log" + args.length + "_" + System.currentTimeMillis() + ".txt");
		try {
			f.createNewFile();
			System.setOut(new PrintStream(f));
			System.setErr(System.out);
			Thread.sleep(50);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(args.length);
		System.out.println("threads = " + Thread.activeCount());
		if(args.length < 1) {
			System.out.println("Port needed !");
			return;
		}
		for(String arg : args)
			System.out.println("arg=" + arg);
		if (args.length > 1) {
			Thread t2 = new Thread(new NavyClient(Integer.valueOf(args[0]), args[1]));
			System.out.println("threads1 = " + Thread.activeCount());
			t2.start();
			System.out.println("threads2 = " + Thread.activeCount());
			try {
				t2.join();
			} catch (InterruptedException e) {
				System.err.print(e);
			}
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
