import java.util.Scanner;

public class Runner {
	final static String filepath = "src//password.txt";
	final static int maxUsernameLength = 12;
	static Scanner scan = new Scanner(System.in);

	public static void main(String[] args) {
		int input = 0;
		do {
			System.out.println("Log in (1), Sign up (2), Delete (3)>>> ");
			try {
				input = scan.nextInt();
				break;
			} catch (Exception e) {
				System.out.println("Input type invalid. Please try again");
				scan.nextLine();
			}
		} while (true);

		MySystem system = new MySystem("src//password.txt");
		String[] credentials = getInput();
		String user = credentials[0];
		String pass = credentials[1];

		if (input == 1) {
			system.logIn(user, pass);
		} else if (input == 2) {
			system.signUp(user, pass);
		} else if (input == 3) {
			system.deleteAccount(user, pass);
		} else {
			System.out.println("Invalid entry.");
		}

		scan.close();

	}

	private static String[] getInput() {
		scan.nextLine();
		System.out.print("Enter your username: ");
		String user = scan.nextLine();
		if (user.length() > maxUsernameLength) {
			user = user.substring(0, maxUsernameLength);
		}
		System.out.print("Enter your password: ");
		String pass = scan.nextLine();
		String[] data = { user, pass };
		return data;
	}

}