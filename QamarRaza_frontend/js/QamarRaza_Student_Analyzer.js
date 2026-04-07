const students = [
  {
    name: "Qamar Raza",
    marks: [
      { subject: "Math", score: 95 },
      { subject: "English", score: 94 },
      { subject: "Science", score: 92 },
      { subject: "History", score: 70 },
      { subject: "Computer", score: 79 }
    ],
    attendance: 95 
  },
  {
    name: "Rishika Sharma",
    marks: [
      { subject: "Math", score: 91 },
      { subject: "English", score: 90 },
      { subject: "Science", score: 95 },
      { subject: "History", score: 55 },
      { subject: "Computer", score: 56 }
    ],
    attendance: 80 
  },
  {
    name: "Gopal Verma",
    marks: [
      { subject: "Math", score: 85 },
      { subject: "English", score: 80 },
      { subject: "Science", score: 35 }, 
      { subject: "History", score: 98 },
      { subject: "Computer", score: 75 }
    ],
    attendance: 88 
  },
  {
    name: "Affan Khan",
    marks: [
      { subject: "Math", score: 70 },
      { subject: "English", score: 75 },
      { subject: "Science", score: 72 },
      { subject: "History", score: 68 },
      { subject: "Computer", score: 80 }
    ],
    attendance: 65 
  },
  {
    name: "Aafab Sheikh",
    marks: [
      { subject: "Math", score: 60 },
      { subject: "English", score: 58 },
      { subject: "Science", score: 62 },
      { subject: "History", score: 55 },
      { subject: "Computer", score: 65 }
    ],
    attendance: 78 
  }
];

function total_marks(array)
{
    for(let i=0;i<array.length;i++)
    {
        let sum=0;
        for(let j=0;j<array[i]["marks"].length;j++)
        {
            sum = sum+array[i]['marks'][j]['score']
        }
        console.log(array[i]["name"]+" Total Marks: "+sum)
    }
}
console.log("--- Total Marks By Each Student ---");
total_marks(students);
console.log("--------------------");


function student_average(array)
{
    for(let i=0;i<array.length;i++)
    {
        const no_of_sub = array[i]["marks"].length;
        let sum=0;
        for(let j=0;j<no_of_sub;j++)
        {
            sum = sum+array[i]['marks'][j]['score']
        }
        try {
            console.log(array[i]["name"]+" Average: "+sum/no_of_sub)
        } catch (error) {
            console.log("No subject")
        }
    }
}
console.log("---Average Marks for Each Student---");
student_average(students)
console.log("--------------------");


function subject_wise_highest_score(array) {
    let no_of_sub = array[0].marks.length;

    for (let i = 0; i < no_of_sub; i++) {
        let highest = array[0].marks[i].score;
        let index = 0;
        let subjectName = array[0].marks[i].subject;

        for (let j = 1; j < array.length; j++) {
            if (array[j].marks[i].score > highest) {
                highest = array[j].marks[i].score;
                index = j;
            }
        }
        console.log("Highest in " + subjectName + ": " + array[index].name + " (" + highest + ")");
    }
}
console.log("---Subject Wise Highest Score---");
subject_wise_highest_score(students)
console.log("--------------------");


function subject_wise_average_score(array) {
    let no_of_subjects = array[0].marks.length;
    let no_of_students = array.length;

    for (let i = 0; i < no_of_subjects; i++) {
        let subjectSum = 0;
        let subjectName = array[0].marks[i].subject;

        for (let j = 0; j < no_of_students; j++) {
            subjectSum += array[j].marks[i].score;
        }

        let avg = subjectSum / no_of_students;
        console.log("Average " + subjectName + " Score: " + avg.toFixed(2));
    }
}
console.log("---Subject Wise Average Score---");
subject_wise_average_score(students);
console.log("--------------------");


function find_topper(array)
{
    let student_total = [null,null,null,null,null]
    for(let i=0;i<array.length;i++)
    {
        let sum=0;
        for(let j=0;j<array[i]["marks"].length;j++)
        {
            sum = sum+array[i]['marks'][j]['score']
        }
        student_total[i] = sum
    }
    //For identifying topper
    let topper = -1;
    let index = 0
    for(let i=0;i<student_total.length;i++)
    {
        if(topper<student_total[i])
        {
            index=i
            topper=student_total[i]
        }
    }
    console.log("Class Topper: "+array[index].name+" with "+topper+" marks")
}
console.log("---Finding Topper---");
find_topper(students)
console.log("--------------------");


function assign_grade(array) {
    for (let i = 0; i < array.length; i++) {
        let student = array[i];

        // 1. Attendance Check (Pehle hi bahar nikal jao)
        if (student.attendance < 75) {
            console.log(student.name + " Grade: Fail (Low Attendance)");
            continue; // Agle student par jao
        }

        let sum = 0;
        let hasFailedInSubject = false; 
        let failedSubject = "";

        // 2. Subject Score Check
        for (let j = 0; j < student.marks.length; j++) {
            if (student.marks[j].score <= 40) {
                hasFailedInSubject = true;
                failedSubject = student.marks[j].subject;
                break; 
            }
            sum += student.marks[j].score;
        }

        // 3. Final Decision
        if (hasFailedInSubject) {
            console.log(student.name + " Grade: Fail (Failed in " + failedSubject + ")");
        } else {
            const avg = sum / student.marks.length;
            
            if (avg >= 85) {
                console.log(student.name + " Grade: A");
            } else if (avg >= 70) {
                console.log(student.name + " Grade: B");
            } else if (avg >= 50) {
                console.log(student.name + " Grade: C");
            } else {
                console.log(student.name + " Grade: Fail");
            }
        }
    }
}
console.log("---Assign Grade---");
assign_grade(students)
console.log("--------------------");