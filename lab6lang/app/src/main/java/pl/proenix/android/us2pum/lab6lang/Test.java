package pl.proenix.android.us2pum.lab6lang;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper object for managing testing of words.
 * Object is parcelable so it can be send via Bundle.
 */
public class Test implements Parcelable {
    private Word _word; // Should be always English word version.
    private int _mode;

    public static final int TEST_MODE_TO_ENGLISH = 10;
    public static final int TEST_MODE_TO_POLISH = 11;
    public static final int TEST_MODE_TO_BOTH = 12; // Not used in Test Object.

    private int _tries = 0;
    private int _result = -1;

    private static final int RESULT_NOT_TESTED = -1;
    private static final int RESULT_TRY_AGAIN = 0;
    private static final int RESULT_PASSED = 10;
    private static final int RESULT_FAILED = 20;

    private int _minDistance = 2048;
    private String _probableAnswer = "";

    public Test() {}

    public Test(Word word, int mode) {
        this._word = word;
        this._mode = mode;
    }

    public int getMode() {
        return this._mode;
    }

    public int getResult() {
        return this._result;
    }

    public int getTries() {
        return this._tries;
    }

    public Word getWord() {
        return this._word;
    }

    /**
     * Check if item leveled up.
     * @return True if item was answered correctly on first try.
     */
    public boolean isAdvanced() {
        return this._tries == 1 && this._result == RESULT_PASSED;
    }

    /**
     * Set test as passed if test was never taken or flag to retry was set and increment tries counter.
     */
    private void setResultPassed() {
        this._tries += 1;
        if (this._result == RESULT_NOT_TESTED || this._result == RESULT_TRY_AGAIN) {
            this._result = RESULT_PASSED;
            this._word.levelUp();
        } else {
            this._result = RESULT_PASSED;
            this._word.levelDown();
        }
    }

    /**
     * Set test as not passed and increment tries counter.
     */
    private void setResultNotPassed() {
        this._tries += 1;
        this._result = RESULT_FAILED;
    }

    /**
     * Set test as not passed to retry without incrementing tries counter.
     */
    private void setResultTryAgain() {
        this._result = RESULT_TRY_AGAIN;
    }

    /**
     * Check whether test should be retried without penalty due to user inputted different writing of word with same meaning.
     * Eg. user input for word "samochód" is "car" but wanted word was "automobile".
     * @return True if test should be retried.
     */
    public boolean isResultTryAgain() {
        return (this._result == RESULT_TRY_AGAIN);
    }

    /**
     * Validate answer and decide if provided string is similar enough to answers.
     *
     * For MODE_TO_POLISH check every word for similarity - allowing small typos.
     * For MODE_TO_ENGLISH enforce that user provide exactly string that is requested.
     * If user provide alternative english word sets flag to retry that word.
     *
     * For comparison all words are transformed to lower case.
     *
     * Uses Levenshtein Distance algorithm to detect strings similarities.
     * @param userAnswer User provided string.
     * @return True if answer is accepted.
     */
    public boolean checkAnswer(String userAnswer) {
        List<Word> wordsToCheck = new ArrayList<Word>();

        // For TO_POLISH check all related words and allow typo in word.
        if (this._mode == TEST_MODE_TO_POLISH) {
            wordsToCheck = this._word.getRelatedWordsOtherLanguage();
            for (Word acceptableAnswer : wordsToCheck) {
                // Calculate Levenshtein distance for word pair.
                int distance = LevenshteinDistance.getDefaultInstance().apply(acceptableAnswer.getName().toLowerCase(), userAnswer.toLowerCase());

                // Calculate acceptable error for word pair.
                int acceptedErrorThreshold = (int) Math.floor(Math.sqrt(acceptableAnswer.getName().length()) - 1.0);

//                Log.d("AndroidLang", "ComparingToPOLISH: {"
//                        + "userAnswer: " + userAnswer + "; "
//                        + "acceptableAnswer: " + acceptableAnswer.getName() + "; "
//                        + "calculatedDistance: " + String.valueOf(distance) + "; "
//                        + "acceptableDistance: " + String.valueOf(acceptedErrorThreshold));

                if (distance == 0) {
                    this._minDistance = 0;
                    this._probableAnswer = this._word.getName();
                    this.setResultPassed();
                    return true;
                } else if (distance <= acceptedErrorThreshold) {
                    this._minDistance = distance;
                    this._probableAnswer = acceptableAnswer.getName();
                    this.setResultPassed();
                    return true;
                }
            }
        }
        if (this._mode == TEST_MODE_TO_ENGLISH) {
            if (userAnswer.toLowerCase().equals(this._word.getName().toLowerCase())) {
                this._minDistance = 0;
                this._probableAnswer = this._word.getName();
                this.setResultPassed();
                return true;
            } else {
                // Check for words with same meaning.
                wordsToCheck = this._word.getRelatedWordsSameLanguage();
                for (Word acceptableAnswer : wordsToCheck) {
                    // Calculate Levenshtein distance for word pair.
                    int distance = LevenshteinDistance.getDefaultInstance().apply(acceptableAnswer.getName().toLowerCase(), userAnswer.toLowerCase());

                    // Calculate acceptable error for word pair.
                    int acceptedErrorThreshold = (int) Math.floor(Math.sqrt(acceptableAnswer.getName().length()) - 1.0);

//                    Log.d("AndroidLang", "ComparingToENGLISH: {"
//                            + "userAnswer: " + userAnswer + "; "
//                            + "acceptableAnswer: " + acceptableAnswer.getName() + "; "
//                            + "calculatedDistance: " + String.valueOf(distance) + "; "
//                            + "acceptableDistance: " + String.valueOf(acceptedErrorThreshold));

                    if (distance <= acceptedErrorThreshold) {
                        this._minDistance = distance;
                        this._probableAnswer = acceptableAnswer.getName();
                        this.setResultTryAgain();
                        return false;
                    }
                }
            }
        }
        this.setResultNotPassed();
        return false;
    }

    /**
     * Return min distance for currently answered test.
     * @return Integer min distance.
     */
    public int getMinDistance() {
        if (this._result == RESULT_PASSED) {
            return this._minDistance;
        }
        return 2048;
    }

    /**
     * Return probable answer if was accepted.
     * @return String probable answer.
     */
    public String getProbableAnswer() {
        if (this._result == RESULT_PASSED) {
            return this._probableAnswer;
        }
        return "";
    }

    /**
     * Return hint what was not accepted if user can try again.
     * @return String Answer that was not accepted.
     */
    public String getRetryHint() {
        if (this._result == RESULT_TRY_AGAIN) {
            return this._probableAnswer;
        }
        return "";
    }

    /**
     * Word getter for displaying.
     * @return String word to display to user interface.
     */
    public String getWordToShow() {
        if (this._mode == TEST_MODE_TO_POLISH) {
            return this._word.getName();
        } else {
            return this._word.getRelatedWordsOtherLanguage().get(0).getName();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(_word);
        dest.writeInt(_mode);
        dest.writeInt(_tries);
        dest.writeInt(_result);
        dest.writeInt(_minDistance);
        dest.writeString(_probableAnswer);
    }


    protected Test(Parcel in) {
        _word = (Word) in.readValue(Word.class.getClassLoader());
        _mode = in.readInt();
        _tries = in.readInt();
        _result = in.readInt();
        _minDistance = in.readInt();
        _probableAnswer = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Test> CREATOR = new Parcelable.Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "Test: {_word: " + _word.getName() + ", mode: "+ _mode +", tries: " + _tries + ";}";
    }
}
