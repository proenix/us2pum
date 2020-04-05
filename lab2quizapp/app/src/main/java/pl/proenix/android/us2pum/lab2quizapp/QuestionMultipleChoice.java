package pl.proenix.android.us2pum.lab2quizapp;

import java.util.ArrayList;
import java.util.List;

public class QuestionMultipleChoice extends Question {
    public List<String> answers;

    public QuestionMultipleChoice(int type, int imageResource, String question, String correctAnswer, String answer0, String answer1, String answer2, String answer3) {
        super(type, imageResource, question, correctAnswer);
        this.answers = new ArrayList<String>();
        this.answers.add(answer0);
        this.answers.add(answer1);
        this.answers.add(answer2);
        this.answers.add(answer3);
    }
}
