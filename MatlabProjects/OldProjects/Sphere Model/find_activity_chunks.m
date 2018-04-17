data = realdisp_quat;
sub_count = length(data);
activity_chunks = [];

for sub = 1:sub_count
    sess_count = length(data(sub).session);
    for sess = 1:sess_count
        labels = data(sub).session(sess).labels;       
             
        count = length(labels);            
        chunks = [];            
        start_index = 1;
        activity = labels(1);
        for i =2:count
            if labels(i) ~= activity
                chunks = [chunks; activity, start_index, i-1];
                start_index = i;
                activity = labels(i);
            end                    
        end
        chunks = [chunks; activity, start_index, count];
        activity_chunks(sub).session(sess).chunks = chunks;                                    
        
    end
end

realdisp_activity_chunks = activity_chunks;
save('realdisp_activity_chunks', 'realdisp_activity_chunks');
