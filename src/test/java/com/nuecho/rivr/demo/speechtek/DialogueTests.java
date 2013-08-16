/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */

package com.nuecho.rivr.demo.speechtek;

import static junit.framework.Assert.*;

import javax.json.*;

import org.junit.*;
import org.slf4j.*;

import com.nuecho.rivr.core.channel.synchronous.step.*;
import com.nuecho.rivr.core.util.*;
import com.nuecho.rivr.voicexml.dialogue.*;
import com.nuecho.rivr.voicexml.rendering.voicexml.*;
import com.nuecho.rivr.voicexml.test.*;
import com.nuecho.rivr.voicexml.turn.first.*;
import com.nuecho.rivr.voicexml.turn.last.*;
import com.nuecho.rivr.voicexml.turn.output.*;

/**
 * @author Nu Echo Inc.
 */
public class DialogueTests {

    private VoiceXmlTestDialogueChannel mDialogueChannel;

    @Before
    public void init() {
        mDialogueChannel = new VoiceXmlTestDialogueChannel("Dialog Tests", TimeValue.seconds(5));
    }

    @Test
    public void testFirstDemoWithSmallNumber() {
        startDialogue(new FirstDemoDialogue(), new VoiceXmlFirstTurn());

        assertEquals("question", mDialogueChannel.getLastStepAsOutputTurn().getName());

        mDialogueChannel.processRecognition(
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                         .add("interpretation", "100"))
                .build());

        assertEquals("feedback-small", mDialogueChannel.getLastStepAsOutputTurn().getName());
        mDialogueChannel.processNoAction();
    }

    @Test
    public void testFirstDemoWithLargeNumber() {
        startDialogue(new FirstDemoDialogue(), new VoiceXmlFirstTurn());

        assertEquals("question", mDialogueChannel.getLastStepAsOutputTurn().getName());

        mDialogueChannel.processRecognition(
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                         .add("interpretation", "2000"))
                .build());

        assertEquals("feedback-large", mDialogueChannel.getLastStepAsOutputTurn().getName());
        mDialogueChannel.processNoAction();
    }

    @Test
    public void testSecondDemoWithLowConfidence() {
        startDialogue(new SecondDemoDialogue(), new VoiceXmlFirstTurn());

        assertEquals("question", mDialogueChannel.getLastStepAsOutputTurn().getName());

        mDialogueChannel.processRecognition(
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                         .add("interpretation", "100")
                         .add("confidence", 0.3))
                .build());
        assertEquals("feedback-repeat", mDialogueChannel.getLastStepAsOutputTurn().getName());

        mDialogueChannel.processNoAction();
        assertEquals("question", mDialogueChannel.getLastStepAsOutputTurn().getName());

        mDialogueChannel.processHangup();
    }

    @Test
    public void testSecondDemoWithHighConfidence() {
        startDialogue(new SecondDemoDialogue(), new VoiceXmlFirstTurn());

        assertEquals("question", mDialogueChannel.getLastStepAsOutputTurn().getName());

        mDialogueChannel.processRecognition(
            Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                         .add("interpretation", "100")
                         .add("confidence", 0.9))
                .build());

        assertEquals("feedback-done", mDialogueChannel.getLastStepAsOutputTurn().getName());
        mDialogueChannel.processNoAction();

    }

    private Step<VoiceXmlOutputTurn, VoiceXmlLastTurn> startDialogue(VoiceXmlDialogue dialogue,
                                                                     VoiceXmlFirstTurn firstTurn) {
        VoiceXmlDialogueContext context = new VoiceXmlDialogueContext(mDialogueChannel,
                                                                      LoggerFactory.getLogger(getClass()),
                                                                      "x",
                                                                      "contextPath",
                                                                      "servletPath");
        return mDialogueChannel.startDialogue(dialogue, firstTurn, context);
    }

    @After
    public void terminate() {
        mDialogueChannel.dispose();
    }

}
