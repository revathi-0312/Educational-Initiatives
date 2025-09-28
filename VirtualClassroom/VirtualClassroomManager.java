import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class VirtualClassroomManager {
    private static final Logger logger = Logger.getLogger("VirtualClassroomManager");

       private Map<String, Classroom> classrooms = new HashMap<>();

    public static void main(String[] args) {
        VirtualClassroomManager manager = new VirtualClassroomManager();
        manager.run();
    }

    private void run() {
        logger.info("Virtual Classroom Manager started.");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter commands (type 'exit' to quit):");

        while (true) {
            try {
                System.out.print("> ");
                String line = sc.nextLine().trim();
                if (line.equalsIgnoreCase("exit")) {
                    System.out.println("Goodbye!");
                    break;
                }
                if (line.isEmpty()) continue;

                processCommand(line);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error processing command", e);
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
        sc.close();
    }

    private void processCommand(String line) {
        String[] parts = line.split("\\s+", 2);
        String command = parts[0];
        String args = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "add_classroom":
                addClassroom(args);
                break;
            case "add_student":
                addStudent(args);
                break;
            case "schedule_assignment":
                scheduleAssignment(args);
                break;
            case "submit_assignment":
                submitAssignment(args);
                break;
            case "list_classrooms":
                listClassrooms();
                break;
            case "list_students":
                listStudents(args);
                break;
            default:
                System.out.println("Unknown command.");
        }
    }

    private void addClassroom(String arg) {
        String className = arg.trim();
        if (className.isEmpty()) {
            System.out.println("Usage: add_classroom <ClassName>");
            return;
        }
        if (classrooms.containsKey(className)) {
            System.out.println("Classroom already exists.");
            return;
        }
        classrooms.put(className, new Classroom(className));
        System.out.println("Classroom " + className + " has been created.");
        logger.info("Classroom created: " + className);
    }

    private void addStudent(String arg) {
        String[] tokens = arg.split("\\s+");
        if (tokens.length < 2) {
            System.out.println("Usage: add_student <StudentID> <ClassName>");
            return;
        }
        String studentId = tokens[0];
        String className = tokens[1];
        Classroom c = classrooms.get(className);
        if (c == null) {
            System.out.println("Classroom not found.");
            return;
        }
        c.addStudent(new Student(studentId));
        System.out.println("Student " + studentId + " has been enrolled in " + className + ".");
        logger.info("Student enrolled: " + studentId + " in " + className);
    }

    private void scheduleAssignment(String arg) {
        String[] tokens = arg.split("\\s+", 2);
        if (tokens.length < 2) {
            System.out.println("Usage: schedule_assignment <ClassName> <AssignmentDetails>");
            return;
        }
        String className = tokens[0];
        String assignmentDetails = tokens[1];
        Classroom c = classrooms.get(className);
        if (c == null) {
            System.out.println("Classroom not found.");
            return;
        }
        c.scheduleAssignment(assignmentDetails);
        System.out.println("Assignment for " + className + " has been scheduled.");
        logger.info("Assignment scheduled for " + className + ": " + assignmentDetails);
    }

    private void submitAssignment(String arg) {
        String[] tokens = arg.split("\\s+", 3);
        if (tokens.length < 3) {
            System.out.println("Usage: submit_assignment <StudentID> <ClassName> <AssignmentDetails>");
            return;
        }
        String studentId = tokens[0];
        String className = tokens[1];
        String assignmentDetails = tokens[2];
        Classroom c = classrooms.get(className);
        if (c == null) {
            System.out.println("Classroom not found.");
            return;
        }
        if (!c.hasStudent(studentId)) {
            System.out.println("Student not enrolled in " + className);
            return;
        }
        c.submitAssignment(studentId, assignmentDetails);
        System.out.println("Assignment submitted by Student " + studentId + " in " + className + ".");
        logger.info("Assignment submitted by " + studentId + " in " + className + ": " + assignmentDetails);
    }

    private void listClassrooms() {
        if (classrooms.isEmpty()) {
            System.out.println("No classrooms available.");
            return;
        }
        System.out.println("Classrooms:");
        classrooms.keySet().forEach(System.out::println);
    }

    private void listStudents(String arg) {
        String className = arg.trim();
        if (className.isEmpty()) {
            System.out.println("Usage: list_students <ClassName>");
            return;
        }
        Classroom c = classrooms.get(className);
        if (c == null) {
            System.out.println("Classroom not found.");
            return;
        }
        c.listStudents();
    }
}

class Classroom {
    private String name;
    private Map<String, Student> students = new HashMap<>();
    private List<String> assignments = new ArrayList<>();

    public Classroom(String name) {
        this.name = name;
    }

    public void addStudent(Student s) {
        students.putIfAbsent(s.getId(), s);
    }

    public boolean hasStudent(String id) {
        return students.containsKey(id);
    }

    public void scheduleAssignment(String details) {
        assignments.add(details);
    }

    public void submitAssignment(String studentId, String details) {
        Student s = students.get(studentId);
        if (s != null) {
            s.submitAssignment(details);
        }
    }

    public void listStudents() {
        if (students.isEmpty()) {
            System.out.println("No students in " + name);
            return;
        }
        System.out.println("Students in " + name + ":");
        students.values().forEach(stu -> System.out.println("- " + stu.getId()));
    }
}

class Student {
    private String id;
    private List<String> submittedAssignments = new ArrayList<>();

    public Student(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void submitAssignment(String details) {
        submittedAssignments.add(details);
    }
}
