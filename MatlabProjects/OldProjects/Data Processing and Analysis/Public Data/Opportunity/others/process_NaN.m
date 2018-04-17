data = opp_data_with_nan;
sub_count = length(data);
opp_data = [];

for sub = 1:sub_count
    sess_count = length(data(sub).session);
    for sess = 1:sess_count
        t = data(sub).session(sess).t;
        labels = data(sub).session(sess).labels;
        
        pos_count = length(data(sub).session(sess).position);
        nanStart = 0;
        for pos = 1:pos_count
            fprintf('Processing subject %d, session %d, pos %d :: ', sub, sess, pos);            
            d = data(sub).session(sess).position(pos).data;
            
            total = length(d);            
            nanFlag = false;            
            for i = 1:total
                row = d(i,:);
                if sum(isnan(row)) ~= 0 
                    if nanFlag == false                     
                        nanFlag = true;
                        if nanStart >0 && i~=nanStart
                            fprintf('Nan count problem intra position');
                            exit;
                        end
                        
                        nanStart = i;
                    end
                elseif nanFlag == true
                        print('\n***************Nan Mixed: %d, %d, %d, %d, %d ****************\n', sub, sess, pos, nanStart, i);                    
                        exit;
                end
            end
            
            total2 = length(d);
            fprintf('%d, %d\n',total, nanStart);             
            
        end
        
        opp_data(sub).session(sess).t = t(1:nanStart-1, :);
        opp_data(sub).session(sess).labels = labels(1:nanStart-1, :);
        
        for pos= 1:pos_count        
            opp_data(sub).session(sess).position(pos).name= data(sub).session(sess).position(pos).name;
            d = data(sub).session(sess).position(pos).data(1:nanStart-1, :);
            d = d/1000;
            d(:, 1:3)= d(:, 1:3)*9.8; 
            opp_data(sub).session(sess).position(pos).data= d;
        end
        
        opp_data(sub).session(sess).original_sample_count = length(d);
        
    end    
end

save('opp_data', 'opp_data');