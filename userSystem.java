import java.util.*;
public class userSystem {
	
	// hashmap containing all user "profiles", which are hashmaps themselves
	public static HashMap<String, HashMap<String, String>> database = new HashMap<String, HashMap<String, String>>();
	
	// hashmap of all activities and their current monitors, if any
	public static HashMap<String, String> activities = new HashMap<String, String>();
	
	// store the current logged in username and profile to easily modify their data and update in database
	public static String currentUser = new String();
	public static HashMap<String, String> currentUserData;
	
	public static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		// this just calls the method to add default info to database and activity map, then the login method
		System.out.println("Welcome to the gym user management system.\n");
		addDefaultUsers();
		logIn();
	}
	
	public static void logIn() {
		// this takes user input for username and password and logs into an existing account, calling that role menu method
		// asking role type is omitted as it is an unnecessary step, the data is already in the user's profile
		/*
		 * 
		 * using a scanner, this method takes the user input of a username, then checks if the username exists in the database
		 * if it doesn't, the method recurses until the user enters a valid username
		 * if it is a valid username, it asks for a password
		 * if the password is correct, the program calls the corresponding method of whatever type of user has just logged in, i.e. client/monitor/admin
		 * 
		 * 
		 */
		System.out.println("------------\n|LOGIN MENU|\n------------");
		System.out.println("Please enter your username:");
		String inputUsername = sc.next().toLowerCase();
		Set<String> unames = database.keySet();
		if (searchMethod(unames, inputUsername)) {
			System.out.println("Please enter your password:");
			String inputPassword = sc.next().toLowerCase();
			currentUser = inputUsername;
			currentUserData = database.get(inputUsername);
			if (inputPassword.equals(currentUserData.get("password"))) {
				String userType = currentUserData.get("role");
				if (userType.equals("client")) {
					System.out.println("You have logged in as client user: " + currentUserData.get("name"));
					clientMenu();
				}
				else if (userType.equals("monitor")) {
					System.out.println("You have logged in as monitor user: " + currentUserData.get("name"));
					monitorMenu();
				}
				else {
					System.out.println("You have logged in as admin user: " + currentUserData.get("name"));
					adminMenu();
				}
			}
			else {
				System.err.println("Invalid password for selected username. Returning to main menu");
			}
		}
		else {
			System.err.println("Invalid username. Returning to main menu.");
		}
		
		logIn();
	}
	
	public static void clientMenu() {
		/*
		 * 
		 * This method displays all of the options a client has available, i.e activity registration, monitor searching, monitor voting, showing personal data or logging out
		 * this method uses a scanner to take user input to select which option the logged in user wants to do
		 * activity registration will implement a manual search method to ensure a valid activity has been selected, and then updates the user's profile in the database if so
		 * monitor searching will use the manual sorting methods implemented and will display all monitors sorted by either name or number of votes
		 * they can vote for a monitor, with a manual search ensuring that the selected monitor exists
		 * the user can also view their personal data or log out
		 * this method recurses after each option is complete, with the exception of logging out
		 */
		System.out.println("1 - Register in an activity\n2 - View all monitors\n3 - Vote for a monitor\n4 - See your info\n5 - Log out\nPlease choose a number from the above options:");
		int selection = sc.nextInt();
		if (selection > 5 || selection < 1) {
			System.err.println("Invalid selection.");
		}
		else {
			if (selection == 1) {
				getActivities();
				String chosenActivity = sc.next().toLowerCase();
				chosenActivity += sc.nextLine().toLowerCase();
				if (searchMethod(activities.keySet(), chosenActivity)) {
					currentUserData.put("activity", chosenActivity);
					database.put(currentUser, currentUserData);
					System.out.println("You have registered in the activity: " + chosenActivity);
				}
				else System.err.println("Invalid activity selected.");
			}
			if (selection == 2) {
				System.out.println("Please choose how to sort the monitors:\n1 - Alphabetically\n2 - By score\n3 - Do not sort");
				int subselection = sc.nextInt();
				if (subselection > 3 || subselection < 1) {
					System.err.println("Invalid selection.");
				}
				else {
					if (subselection == 1) {
						sortUsers("monitor", "alphabetically");
					}
					if (subselection == 2) {
						sortUsers("monitor", "votes");
					}
					else if (subselection == 3) {
						sortUsers("monitor", "unsorted");
					}
				}
			}
			if (selection == 3) {
				HashMap<String, HashMap<String, String>> users = sortUsers("monitor", "unsorted");
				System.out.println("Please enter the username of the monitor you want to vote for from the above:");
				String selectedUser = sc.next().toLowerCase();
				if (!searchMethod(users.keySet(), selectedUser)) {
					System.err.println("Invalid username selected.");
				}
				else {
					System.out.println("What score (1-12) would you like to give this monitor?");
					int score = sc.nextInt();
					if (score > 12 || score < 1) {
						System.err.println("Invalid score selected. Must be between 1 and 12 inclusive.");
					}
					else voteUser(selectedUser, score);
				}
			}
			if (selection == 4) {
				printUser(currentUser);
			}
			if (selection == 5) {
				System.err.println("Logging out.");
				return;
			}
		}
		clientMenu();
	}
	
	public static void monitorMenu() {
		/*
		 * this method will show the monitor their options, and use a scanner to allow selection
		 * they can view all clients sorted by name or by votes - which will implement manual sorting methods
		 * they can vote for a client - this will implement a manual search to ensure the client exists in the system
		 * they can view their personal data or log out
		 * this method will always recurse after each option completes unless the user decides to log out
		 */
		System.out.println("1 - View all clients\n2 - Vote for a client\n3 - Show your info\n4 - Log out\nPlease choose a number from the above options:");
		int selection = sc.nextInt();
		if (selection > 4 || selection < 1) {
			System.err.println("Invalid selection.");
		}
		else {
			if (selection == 1) {
				System.out.println("Please choose how to sort the clients:\n1 - Alphabetically\n2 - By score\n3 - Do not sort");
				int subselection = sc.nextInt();
				if (subselection > 3 || subselection < 1) {
					System.err.println("Invalid selection.");
				}
				else {
					if (subselection == 1) {
						sortUsers("client", "alphabetically");
					}
					if (subselection == 2) {
						sortUsers("client", "votes");
					}
					else if (subselection == 3) {
						sortUsers("client", "unsorted");
					}
				}
			}
			if (selection == 2) {
				HashMap<String, HashMap<String, String>> users = sortUsers("client", "unsorted");
				System.out.println("Please enter the username of the client you want to vote for from the above:");
				String selectedUser = sc.next().toLowerCase();
				if (!searchMethod(users.keySet(), selectedUser)) {
					System.err.println("Invalid username selected.");
				}
				else {
					System.out.println("What score (1-12) would you like to give this client?");
					int score = sc.nextInt();
					if (score > 12 || score < 1) {
						System.err.println("Invalid score selected. Must be between 1 and 12 inclusive.");
					}
					else voteUser(selectedUser, score);
				}
			}
			if (selection == 3) {
				printUser(currentUser);
			}
			if (selection == 4) {
				System.err.println("Logging out.");
				return;
			}
		}
		monitorMenu();
	}
	
	public static void adminMenu() {
		/*
		 * this method will display all options available to an admin user and will use a scanner to allow selection of an option
		 * it will allow an admin to add a new client or monitor user, which calls a specific method to do that
		 * it allows an admin to add a new activity to the activity list, which will check whether the activity already exists or not using a manual search
		 * allows an admin to select a monitor (which will use the manual search method) and then enroll them in an activity (which also will use the manual search)
		 * allows the admin to update either all client levels or monitor levels, which calls a specific method to do that and displays all levels of the chosen user type
		 * recurses after each option completes unless the user chooses to log out
		 */
		String[] adminOpts = {
				"Add a new client to the system",
				"Add a new monitor to the system",
				"Add a new activity to the system",
				"Enroll a monitor to an activity",
				"Update client levels",
				"Update monitor levels", 
				"Log out"
		};
		for (int i=0; i < adminOpts.length; i++) {
			System.out.println((i+1)+ " - " + adminOpts[i]);
		}
		System.out.println("Please choose a number from the above options:");
		int selection = sc.nextInt();
		if (selection > 7 || selection < 1) {
			System.err.println("Invalid selection.");
		}
		else {
			if (selection == 1) {
				newUser("client");
			}
			if (selection == 2) {
				newUser("monitor");
			}
			if (selection == 3) {
				System.out.println("Currently registered activities:");
				getActivities();
				System.out.println("Please enter the name of the new activity:");
				String newActiv = sc.next().toLowerCase();
				newActiv += sc.nextLine().toLowerCase();
				if (searchMethod(activities.keySet(), newActiv)) {
					System.err.println("Activity already present in system.");
				}
				else {
					activities.put(newActiv, "None");
					System.out.println("New activity \"" + newActiv + "\" added to the system.");
				}
			}
			if (selection == 4) {
				HashMap<String, HashMap<String, String>> monitors = sortUsers("monitor", "unsorted");
				System.out.println("Please choose the username of the monitor you would like to enroll to an activity:");
				String monName = sc.next().toLowerCase();
				if (searchMethod(monitors.keySet(), monName)) {
					System.out.println("Please choose the name of the activity you want to enroll this monitor to:");
					getActivities();
					String chosenActivity = sc.next().toLowerCase();
					chosenActivity += sc.nextLine().toLowerCase();
					if (searchMethod(activities.keySet(), chosenActivity)) {
						// following if statement checks if there is an enrolled monitor, so we can update their profile
						if (!(activities.get(chosenActivity).equalsIgnoreCase("none"))) {
							HashMap<String, String> currentMonitor = database.get(activities.get(chosenActivity));
							currentMonitor.put("activity", "none");
							database.put(currentMonitor.get("username"), currentMonitor);
						}
						// following if statement checks if the monitor is already assigned to another activity, so we can unenroll them
						Set<String> activSet = new HashSet<String>();
						for (String a: activities.values()) {
							activSet.add(a);
						}
						if (searchMethod(activSet, monName)) {
							for (String activ: activities.keySet()) {
								if (activities.get(activ).equalsIgnoreCase(monName)) {
									activities.put(activ, "none");
								}
							}
						}
						activities.put(chosenActivity, monName);
						HashMap<String, String> selectedMonitor = database.get(monName);
						selectedMonitor.put("activity", chosenActivity);
						database.put(monName, selectedMonitor);
						System.out.println(monName + " has been enrolled to monitor the activity: " + chosenActivity);
					}
					else System.err.println("Invalid activity selected");
				}
				else System.err.println("Invalid monitor username selected.");
			}
			if (selection == 5) {
				updateLvl("client");
			}
			if (selection == 6) {
				updateLvl("monitor");
			}
			if (selection == 7) {
				System.err.println("Logging out.");
				return;
			}
		}
		
		adminMenu();
	}
	
	public static void voteUser(String uName, int voteAmount) {
		/*
		 * this method will be called by other parts of the program, and will update in the database the votes of the user whose username is passed in the arguments
		 */
		HashMap<String, String> profile = database.get(uName);
		int currentVotes = Integer.parseInt(profile.get("votes"));
		currentVotes += voteAmount;
		profile.put("votes", Integer.toString(currentVotes));
		database.put(uName, profile);
		System.out.println("\n" + uName + " votes increased by " + voteAmount + "\n");
	}
		
	public static void updateLvl(String roleType) {
		// this will update the levels of either all clients or all monitors, based on their votes
		// if the users votes are under 21, they are considered beginner, between 21 and 50 inclusive they are considered advanced and 51 up is considered expert
		// updates the profiles in the database and also prints the current level of each user
		HashMap<String, String> currentProf;
		for (String uname: database.keySet()) {
			currentProf = database.get(uname);
			if (currentProf.get("role").equalsIgnoreCase(roleType)) {
				int votes = Integer.parseInt(currentProf.get("votes"));
				if (votes >= 51) {
					currentProf.put("level", "expert");
				}
				else if (votes >= 21 && votes < 51) {
					currentProf.put("level", "advanced");
				}
				else currentProf.put("level", "beginner");
				System.out.println("User " + uname + " has a score of " + votes + " so is considered " + currentProf.get("level"));
				database.put(uname, currentProf);
			}
		}
		System.out.println("-----\nAll user levels of selected type updated.\n-----");
	}
	
	public static void newUser(String userType) {
		/*
		 * using a scanner, this method will prompt the user for details to create a new user profile. 
		 * the method will only ask for the details relevant to the type of account you are creating
		 * These details will be added to a new hashmap which is inserted to the main database once complete, with the key being the username
		 */
		System.out.println("Please enter the details for the new account you are registering. You have chosen to register a new " + userType + ".\n-----");
		HashMap<String, String> newProfile = new HashMap<String, String>();
		newProfile.put("role", userType);
		System.out.println("Please enter the username for the new user (case insensitive, no spaces):");
		String newUname = sc.next().toLowerCase();
		if (searchMethod(database.keySet(), newUname)) {
			System.err.println("A member already exists in the system with that username.");
			newUser(userType);
		}
		else {
			newProfile.put("username", newUname);
			System.out.println("Enter the password for the new account (case insensitive, no spaces):");
			String newPass = sc.next().toLowerCase();
			newProfile.put("password", newPass);
			System.out.println("Enter the full name for the new account:");
			String newName = sc.next();
			newName += sc.nextLine();
			newProfile.put("name", newName);
			if (userType.equalsIgnoreCase("monitor")) {
				newProfile.put("activity", "none");
				newProfile.put("votes", "0");
				newProfile.put("level", "beginner");
				System.out.println("-----\nThis new monitor isn't monitoring any activity. An admin user will need to assign them to an activity.\n-----");
			}
			
			else if (userType.equalsIgnoreCase("client")) {
				getActivities();
				System.out.println("Please choose which activity you are going to enroll the new client in from the above activities:");
				String chosenActivity = sc.next().toLowerCase();
				chosenActivity += sc.nextLine().toLowerCase();
				if (searchMethod(activities.keySet(), chosenActivity)) {
					newProfile.put("activity", chosenActivity);
				}
				else {
					System.err.println("Invalid activity selected. Please start again.");
					newUser(userType);
				}
				
				System.out.println("Please enter how many hours on average the client spends in the gym per week:");
				String avgHours = sc.next().toLowerCase();
				newProfile.put("avgHours", avgHours);
				newProfile.put("votes", "0");
				newProfile.put("level", "beginner");
			}
			System.out.println("New user added successfully.");			
			database.put(newUname, newProfile);
			printUser(newUname);
			
		}
	}
	
	public static HashMap<String, HashMap<String, String>> sortUsers(String userType, String sortType) {
		// this will print all of the clients or monitors, sorted either alphabetically or by votes
		// it will return the data in an unsorted form solely for searching purposes, as order is not required for that
		// when implemented, it will use arrays to easily apply the sorting algorithms and print the results (and because hashmaps have no order), but it will still return a hashmap
		
		// this array will contain usernames only, and will be sorted by the following statements. The code will use the usernames to retrieve full names and votes from the database
		String[] sortedUsernames = new String[15];
				
		// this hashmap will contain all the users of either client type or monitor type, and will be returned 
		HashMap<String, HashMap<String, String>> selectedUsers = new HashMap<String, HashMap<String, String>>();
		for (String username: database.keySet()) {
			HashMap<String, String> profile = database.get(username);
			if (profile.get("role").equalsIgnoreCase(userType)) {
				selectedUsers.put(username, profile);
			}
		}
		int idx = 0;
		for (String uname: selectedUsers.keySet()) {
			sortedUsernames[idx] = uname;
			idx++;
		}
		
		if (sortType.equalsIgnoreCase("alphabetically")) {
			System.out.println("Here are all the requested users, sorted alphabetically:\n-----");

			for (int i = 0; i < sortedUsernames.length; i++) {
				for (int j = i+1; j < sortedUsernames.length; j++) {
					String name1, name2, aux;
					if (sortedUsernames[j] == null || sortedUsernames[i] == null) {
						continue;
					}
					else {
						name1 = selectedUsers.get(sortedUsernames[j]).get("name");
						name2 = selectedUsers.get(sortedUsernames[i]).get("name");
					}
					if (name2.compareTo(name1) > 0){
						aux = sortedUsernames[i];
						sortedUsernames[i] = sortedUsernames[j];
						sortedUsernames[j] = aux;
						}
					}
				}
			
			}
		
			
			
		
		else if (sortType.equalsIgnoreCase("votes")) {
			System.out.println("Here are all the requested users, sorted by number of votes:\n-----");
			for (int i = 0; i < sortedUsernames.length; i++) {
				for (int j = i+1; j < sortedUsernames.length; j++) {
					int vote1, vote2;
					String aux;
					if (sortedUsernames[j] == null || sortedUsernames[i] == null) {
						continue;
					}
					else {
						vote1 = Integer.parseInt(selectedUsers.get(sortedUsernames[j]).get("votes"));
						vote2 = Integer.parseInt(selectedUsers.get(sortedUsernames[i]).get("votes"));
					}
					if (vote1 > vote2){
						aux = sortedUsernames[i];
						sortedUsernames[i] = sortedUsernames[j];
						sortedUsernames[j] = aux;
						}
					}
				}
		}
		
		else if (sortType.equalsIgnoreCase("unsorted")) {
			for (String username: selectedUsers.keySet()) {
				HashMap<String, String> profile = selectedUsers.get(username);
				System.out.println(profile.get("name") + "\n- " + profile.get("votes") + "\n- " + username + "\n-----");
			}
		}
		
		if (!sortType.equalsIgnoreCase("unsorted")) {
			for (String username: sortedUsernames) {
				HashMap<String, String> profile = selectedUsers.get(username);
				if (profile != null) {
					System.out.println(profile.get("name") + "\n- " + profile.get("votes") + "\n- " + username + "\n-----");
				}
			}
		}
		return selectedUsers;
	}

	public static boolean searchMethod(Set<String> items, String search) {
		for (String s: items) {
			if (search.equalsIgnoreCase(s)) return true;
		}
		
		return false;
	}
	
	public static void printUser(String uname) {
		// this method simply prints a user profile, retrieving it from the database based on the username passed
		System.out.println("User info for: " + uname);
		System.out.println("-----");
		HashMap<String, String> profile = database.get(uname);
		for (String key: profile.keySet()) {
			System.out.println(key + ": " + profile.get(key));
		}
		System.out.println("-----");
	}
	
	public static void getActivities() {
		// this prints all available activities and their monitors if any are assigned
		System.out.println("Activity\tMonitor\n--------\t-------");
		for (String activ : activities.keySet()) {
			String monName;
			// the following just gets the full name of the assigned monitor (if they exist in the database)
			if (searchMethod(database.keySet(), activities.get(activ))) {
				monName = database.get(activities.get(activ)).get("name");
			}
			else monName = activities.get(activ);
			System.out.println(activ + "\t\t" + monName);
		}
	}
	
	public static void addDefaultUsers() {
		// used on initialization, to add some default profiles to the system
		HashMap<String, String> newAdmin = new HashMap<String, String>();
		newAdmin.put("name", "Jeff Bezos");
		newAdmin.put("role", "admin");
		newAdmin.put("username", "admin1");
		newAdmin.put("password", "test");
		database.put("admin1", newAdmin);
		newAdmin = new HashMap<String, String>();
		newAdmin.put("name", "Bernard Arnault");
		newAdmin.put("role", "admin");
		newAdmin.put("username", "admin2");
		newAdmin.put("password", "test");
		database.put("admin2", newAdmin);
		
		HashMap<String, String> newMonitor = new HashMap<String, String>();
		newMonitor.put("name", "Bruce Lee");
		newMonitor.put("role", "monitor");
		newMonitor.put("username", "monitor1");
		newMonitor.put("password", "test");
		newMonitor.put("activity", "karate");
		newMonitor.put("votes", "25");
		newMonitor.put("level", "advanced");
		database.put("monitor1", newMonitor);
		newMonitor = new HashMap<String, String>();
		newMonitor.put("name", "Conor McGregor");
		newMonitor.put("role", "monitor");
		newMonitor.put("username", "monitor2");
		newMonitor.put("password", "test");
		newMonitor.put("activity", "mma");
		newMonitor.put("votes", "32");
		newMonitor.put("level", "advanced");
		database.put("monitor2", newMonitor);
		newMonitor = new HashMap<String, String>();
		newMonitor.put("name", "Aaron Davis");
		newMonitor.put("role", "monitor");
		newMonitor.put("username", "mon3");
		newMonitor.put("password", "test");
		newMonitor.put("activity", "None");
		newMonitor.put("votes", "10");
		newMonitor.put("level", "beginner");
		database.put("mon3", newMonitor);
		newMonitor = new HashMap<String, String>();
		newMonitor.put("name", "Adam Keane");
		newMonitor.put("role", "monitor");
		newMonitor.put("username", "mon4");
		newMonitor.put("password", "test");
		newMonitor.put("activity", "None");
		newMonitor.put("votes", "14");
		newMonitor.put("level", "beginner");
		database.put("mon4", newMonitor);
		newMonitor = new HashMap<String, String>();
		newMonitor.put("name", "Xavier Johnson");
		newMonitor.put("role", "monitor");
		newMonitor.put("username", "mon5");
		newMonitor.put("password", "test");
		newMonitor.put("activity", "None");
		newMonitor.put("votes", "13");
		newMonitor.put("level", "beginner");
		database.put("mon5", newMonitor);
		
		HashMap<String, String> newClient = new HashMap<String, String>();
		newClient.put("name", "Joe Bloggs");
		newClient.put("role", "client");
		newClient.put("username", "client1");
		newClient.put("password", "test");
		newClient.put("activity", "karate");
		newClient.put("votes", "3");
		newClient.put("level", "beginner");
		newClient.put("avgHours", "10");
		database.put("client1", newClient);
		newClient = new HashMap<String, String>();
		newClient.put("name", "Adam Parker");
		newClient.put("role", "client");
		newClient.put("username", "client2");
		newClient.put("password", "test");
		newClient.put("activity", "mma");
		newClient.put("votes", "15");
		newClient.put("level", "beginner");
		newClient.put("avgHours", "8");
		database.put("client2", newClient);
		newClient = new HashMap<String, String>();
		newClient.put("name", "Pablo Fernandez");
		newClient.put("role", "client");
		newClient.put("username", "client3");
		newClient.put("password", "test");
		newClient.put("activity", "cycling");
		newClient.put("votes", "20");
		newClient.put("level", "beginner");
		newClient.put("avgHours", "18");
		database.put("client3", newClient);
		
		// add some activities to the system
		activities.put("karate", "monitor1");
		activities.put("mma", "monitor2");
		activities.put("cycling", "None");
		activities.put("rowing", "None");
	}

}
