function [indices, labels_baseline] = get_bite_sip_indices(oppdata, window_size)
    w = window_size/2;
    labels_baseline = [];
    indices = [];
    for sub = 1:4    
        for sess = 1:6
            labels = oppdata{sub,sess}.labels;       
            count = size(labels, 1);
            nlb = zeros(count, 1);            
            
            start_index = 1;
            activity = labels(1, 5); %for right lower arm

            for i =2:count
                if labels(i, 5) ~= activity                                        
                    if activity == 407 || activity == 409
                        m = round((start_index + i -1)/2);
                        indices = [indices; sub, sess, start_index, i-1, m, activity];
                        nlb(m-w:m+w, 1) = 1;
                    end                    
                    start_index = i;
                    activity = labels(i, 5);
                end            
            end
            
            if activity == 407 | activity == 409
                m = round((start_index + count)/2);
                indices = [indices; sub, sess, start_index, count, m, activity];
                nlb(m-w:m+w, 1) = 1;
            end            
            
            labels_baseline{sub,sess} = nlb;
                      
        end
    end
    
end


