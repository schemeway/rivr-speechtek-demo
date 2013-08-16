/*
 * Copyright (c) 2013 Nu Echo Inc. All rights reserved.
 */

package com.nuecho.rivr.demo.speechtek;

import static com.nuecho.rivr.core.dialogue.DialogueUtils.*;
import static com.nuecho.rivr.voicexml.turn.input.VoiceXmlEvent.*;
import static com.nuecho.rivr.voicexml.turn.output.interaction.InteractionBuilder.*;

import com.nuecho.rivr.core.util.*;
import com.nuecho.rivr.voicexml.dialogue.*;
import com.nuecho.rivr.voicexml.rendering.voicexml.*;
import com.nuecho.rivr.voicexml.turn.first.*;
import com.nuecho.rivr.voicexml.turn.input.*;
import com.nuecho.rivr.voicexml.turn.last.*;
import com.nuecho.rivr.voicexml.turn.output.*;
import com.nuecho.rivr.voicexml.turn.output.audio.*;
import com.nuecho.rivr.voicexml.turn.output.grammar.*;
import com.nuecho.rivr.voicexml.turn.output.interaction.*;

/**
 * @author Nu Echo Inc.
 */
public final class SecondDemoDialogue implements VoiceXmlDialogue {

    @Override
    public VoiceXmlLastTurn run(VoiceXmlFirstTurn firstTurn, VoiceXmlDialogueContext context) throws Exception {
        SpeechRecognitionConfiguration recConfig = new SpeechRecognitionConfiguration(new GrammarReference("builtin:grammar/number"));

        InteractionTurn question = newInteractionBuilder("question").addPrompt(new SynthesisText("Say a number."))
                                                                    .build(recConfig, TimeValue.seconds(5));
        boolean done = false;

        do {

            VoiceXmlInputTurn answer = doTurn(context, question);

            if (!hasEvent(NO_INPUT, answer.getEvents()) && !hasEvent(NO_MATCH, answer.getEvents())) {

                double score = answer.getRecognitionInfo()
                                     .getRecognitionResult()
                                     .getJsonObject(0)
                                     .getJsonNumber("confidence")
                                     .doubleValue();

                if (score >= 0.6) {
                    doTurn(context, new MessageTurn("feedback-done", new SynthesisText("Thanks!")));
                    done = true;
                } else {
                    doTurn(context, new MessageTurn("feedback-repeat", new SynthesisText("Can you repeat that?")));
                }
            }

        } while (!done);

        return new VoiceXmlExitTurn("exit");
    }
}
