package pl.proenix.android.us2pum.lab2quizapp;

/**
 * Class for storing Question structure.
 */
public class Question {

    public static final int QUESTION_INPUT = 18;
    public static final int QUESTION_MULTI = 158;

    public int type;
    public int imageResource;
    public String question;
    public String correctAnswer;

    public Question(int type, int imageResource, String question, String correctAnswer) {
        this.type = type;
        this.imageResource = imageResource;
        this.question = question;
        this.correctAnswer = correctAnswer;
    }

    /**
     * Check if question is multiple choice.
     * @return boolean True if question is multiple choice.
     */
    public boolean isMultipleChoice()
    {
        if (type == QUESTION_MULTI) {
            return true;
        }
        return false;
    }
}
