function [conf_mat, prob_scores] = classification_conf_mat(model, test_data, test_labels, label_values)        
    [y, prob_scores] = predict(model, test_data);
    y = str2double(y);    
    
    len = length(label_values);
    conf_mat = zeros(len, len);
    
    for i = 1:len
        for j = 1:len
            conf_mat(i, j) = sum(test_labels == label_values(i) & y == label_values(j));
        end
    end
    
end