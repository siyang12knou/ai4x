package com.kailoslab.ai4x.logic.service;

import com.kailoslab.ai4x.logic.code.ProgramLang;
import com.kailoslab.ai4x.logic.data.LogicFragmentRepository;
import com.kailoslab.ai4x.logic.data.LogicRepository;
import com.kailoslab.ai4x.logic.data.entity.LogicEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LogicService {

    private final Map<String, Object> sessionContext;
    private final Map<ProgramLang, LanguageHelper> languageHelperMap;
    private final LogicRepository logicRepository;
    private final LogicFragmentRepository logicFragmentRepository;

    public LogicService(LogicRepository logicRepository, LogicFragmentRepository logicFragmentRepository) {
        this.logicRepository = logicRepository;
        this.logicFragmentRepository = logicFragmentRepository;
        sessionContext = new HashMap<>();
        languageHelperMap = new HashMap<>(ProgramLang.values().length);
    }

    public LogicEntity saveLogic(LogicEntity logic) {
        return logicRepository.save(logic);
    }

    public void printErrorLog(String message, Throwable e) {
        log.error(message, e);
    }

    static class StringOutputStream extends OutputStream {

        private StringBuffer sb;
        private boolean isErr;

        public StringOutputStream() {
            this(new StringBuffer());
        }

        public StringOutputStream(boolean isErr) {
            this(new StringBuffer(), isErr);
        }

        public StringOutputStream(StringBuffer sb) {
            this(sb, false);
        }

        public StringOutputStream(StringBuffer sb, boolean isErr) {
            this.sb = sb;
            this.isErr = isErr;
        }

        public void close() throws IOException {
            sb = new StringBuffer();
        }

        public void flush() throws IOException {
            if(isErr) {
                log.error(sb.toString());
            } else {
                log.info(sb.toString());
            }
            sb.delete(0, sb.length());
        }

        public void write(byte[] bArray) throws IOException {
            for (byte b: bArray) {
                write(b);
            }
        }

        public void write(byte b) throws IOException {
            if((char)b == '\n') {
                flush();
            } else {
                sb.append((char) b);
            }
        }

        public void write(int i) throws IOException {
            if(i == '\n') {
                flush();
            } else {
                sb.append((char) i);
            }
        }

        public String getData() {
            return sb.toString();
        }
    }
}
