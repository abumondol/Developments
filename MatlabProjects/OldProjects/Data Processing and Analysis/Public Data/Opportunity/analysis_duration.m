mean_durations = [];
for act=401:413
    durations = [];
    for sub = 1:4    
        for sess = 1:6        
            c = activity_chunks(sub).session(sess).chunks_lr;
            c = c(c(:,3)==act , :);
            c = c(:,2) - c(:,1) + 1;               
            durations =[durations; c];
        end
    end
    mean_durations =[mean_durations; act, mean(durations/30)];
end

act_names = {'unlock', 'stir', 'lock', 'close', 'reach', 'open', 'sip', 'clean', 'bite', 'cut', 'spread', 'release', 'move'};
barh(mean_durations(:,2))
xlabel('Average duration (sec)');
grid on
set(gca,'yticklabel',act_names)
return

durations = durations/30;
mean(durations)
figure
histogram(durations, 50);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% discrd rate for NAN %%%%%%%%%%%%%%%%%%% 
data = oppdata;
total = 0;
total2 = 0;
total_right = 0;
total_right_2 = 0;
for subj =1:4
    for sess=1:6
        total = total + data{subj, sess}.total_row;
        total2 = total2 + length(data{subj, sess}.t);
        
        total_right = total_right + sum(data{subj, sess}.label_original(:,5)>0);
        total_right_2 = total_right_2 + sum(data{subj, sess}.labels(:,5)>0);
    end    
end

ratio = 100*(total-total2)/total;
fprintf('%d, %d, %.2f\n', total, total2, ratio);

ratio = 100*(total_right-total_right_2)/total_right;
fprintf('%d, %d, %.2f\n', total_right, total_right_2, ratio);
%%%%% discard for right 






