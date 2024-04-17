package com.mcmp.dummybe.service.abnormal;

import com.mcmp.dummybe.dao.abnormal.AbnormalDAO;
import com.mcmp.dummybe.model.ResultModel;
import com.mcmp.dummybe.model.abnormal.AbnormalDataModel;
import com.mcmp.dummybe.model.abnormal.AbnormalItemModel;
import com.mcmp.dummybe.model.abnormal.AbnormalMessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AbnormalService {
    @Autowired
    private AbnormalDAO abnormalDAO;

    public ResultModel getAbnormal() {
        ResultModel result = new ResultModel();

        List<AbnormalDataModel> abnormalDataList = abnormalDAO.selectAbnormalData();
        List<AbnormalMessageModel> messageList = abnormalDAO.selectAbnormalMessage();
        List<String> itemList2 = abnormalDAO.selectAbnormalItem();


        for (int i = 0; i < abnormalDataList.size(); i++) {
            AbnormalDataModel dataModel = abnormalDataList.get(i);
            if (i < messageList.size()) {
                AbnormalMessageModel message = messageList.get(i);
                message.setItem(splitString(itemList2.get(i)));

                dataModel.setMessage(message);
            }

        }

        result.setData(abnormalDataList);
        return result;
    }
    private List<String> splitString(String target){
        String [] change = target.split(",");
        return Arrays.asList(change);
    }
}
