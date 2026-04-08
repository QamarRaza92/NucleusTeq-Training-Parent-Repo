class Student {
    private String name;
    private String roll_no;
    private float marks;

    Student(String name, String roll_no, float marks) {
        this.name = name;
        this.roll_no = roll_no;
        this.marks = marks;
    }

    //Encapsulation
    public void showData() {
        System.out.println("Student Data:-");
        System.out.println("Student Name: " + name);
        System.out.println("Student Roll No: " + roll_no);
        System.out.println("Student Marks: " + marks);
    }
}

//Inheritance
class GraduateStudent extends Student {
    private String placement_status;
    private int year_of_passing;

    GraduateStudent(String name, String roll_no, float marks, String placement_status, int year_of_passing) {
        super(name, roll_no, marks);
        this.placement_status = placement_status;
        this.year_of_passing = year_of_passing;
    }

    //Polymorphism
    @Override
    public void showData() {
        super.showData();
        System.out.println("Graduation Detail:-");
        System.out.println("Placement Status: " + placement_status);
        System.out.println("Year Of Passing: " + year_of_passing);
    }
}

public class OOP {
    public static void main(String[] args) {
        GraduateStudent gs1 = new GraduateStudent("Qamar Raza", "EN22CS303039", 9.1f, "Placed", 2026);
        gs1.showData();
    }
}