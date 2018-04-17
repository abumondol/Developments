function [conf_mat, scores] = classification_test(model, test_data, test_labels, label_types)    
    %y = model.predict(test_data);
    y = predict(model, test_data);
    %y = str2double(y);
    
    %waitforbuttonpress
    
    len = length(label_types);
    conf_mat = zeros(len, len);
    
    for i = 1:len
        for j = 1:len
            conf_mat(i, j) = sum(test_labels == label_types(i) & y == label_types(j));
        end
    end
    
    scores = [];
    for i=1:len
        TP = conf_mat(i,i);
        FN = sum(conf_mat(i,:)) - TP;
        FP = sum(conf_mat(:,i)) - TP;
        P = 100*TP / (TP + FP);
        R = 100*TP / (TP + FN);
        F1 = 2*P*R/(P+R);
        scores(i).TP = TP;
        scores(i).FP = FP;
        scores(i).FN = FN;
        
        scores(i).P = P;
        scores(i).R = R;
        scores(i).F1 = F1;
        
    end
    
    
end