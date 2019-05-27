package com.example.trapic_test;

import org.junit.Test;
import org.mockito.Matchers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CommentsActivityTest {

    String cmt_pub, cmt_msg, expected_result;

    @Test
    public void testCommentWithTestData(){

        CommentsActivity commentsActivity = new CommentsActivity();

        cmt_pub = "Ezekiel Kenn";
        cmt_msg = "Wow cool post, thanks for sharing.";

        expected_result = "Comment Submitted.";

        String result = commentsActivity.addComment(cmt_pub, cmt_msg);

        assertThat(result, is(expected_result));


    }

}