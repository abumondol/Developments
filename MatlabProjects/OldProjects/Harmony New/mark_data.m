sub_count = length(hw);
fprintf('Marking data... Total sub_count: %d\n', sub_count-1);
for i=1:sub_count
    episode_count = length(hw(i).data);
    
    for j= 1:episode_count
%         if hw(i).data(j).processed > 0
%             fprintf('Already Processed: %s \n', hw(i).data(j).file_name);
%             continue;
%         else
%             fprintf('Processing: %s \n', hw(i).data(j).file_name);
%         end
         
        data = hw(i).data(j).raw_data;
        acc = data(data(:,2)== 1, :);        
        size(acc)
        [a, b] = mark_data_single_episode(acc(:,4:6))                      
        stime = acc(a,1);
        etime = acc(b,1);
                
        hw(i).data(j).stime = stime;
        hw(i).data(j).etime = etime;        
        hw(i).data(j).duration = etime - stime;        
        
        hw(i).data(j).processed = 1;       
        
    end % end of episode_count    
    
end %end of sub_count

save('hw','hw');
fprintf('marking data done\n\n');

