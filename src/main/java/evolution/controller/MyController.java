package evolution.controller;

import evolution.controller.annotation.MyPostMapping;
import evolution.controller.annotation.MyRequestBody;
import evolution.controller.bean.SuccessResponse;
import evolution.controller.bean.Survey;
import jakarta.servlet.http.HttpServletRequest;

public class MyController {
    @MyPostMapping("fosu")
    public SuccessResponse submitFosuSurvey(@MyRequestBody Survey survey, HttpServletRequest httpServletRequest) {
        return new SuccessResponse(true,
                "我是" + survey.name + "，学号为" + survey.id + "，我对佛大的建议是" + survey.suggestionForFosu + "。");
    }

    @MyPostMapping("grad")
    public SuccessResponse submitGraduationSurvey(@MyRequestBody Survey survey, HttpServletRequest httpServletRequest) {
        return new SuccessResponse(true,
                "我是" + survey.name + "，学号为" + survey.id + "，我对毕业后的规划是" + survey.planForGraduation + "。");
    }
}
