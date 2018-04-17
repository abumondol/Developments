function length_analysis(names, signatures)
    sign_stat=struct;
    
    total_names = length(names);
    total_signatures = length(signatures); 
    sign_allsub_index = [signatures.subject_index];
    durations = [signatures.duration];
    durations = durations/1e9;
    
    sign_stat.total_subjects = total_names;
    sign_stat.total_signatures = total_signatures;
    
    for i = 1: total_names
        sub_sign_index = find(sign_allsub_index==i);
        sub_sign_durations = durations(sub_sign_index);
        sign_stat.subject(i).total_signatures = length(sub_sign_index);
        sign_stat.subject(i).durations = sub_sign_durations;
        sign_stat.subject(i).min = min(sub_sign_durations);
        sign_stat.subject(i).max = max(sub_sign_durations);
        sign_stat.subject(i).avg = mean(sub_sign_durations);
        sign_stat.subject(i).std = std(sub_sign_durations);
        m = sign_stat.subject(i).avg;
        s = sign_stat.subject(i).std;
        mn = sign_stat.subject(i).min;
        mx = sign_stat.subject(i).max;        
        fprintf('Subject:%d, mean=%.3f, std=%.3f, min=%.3f, max=%.3f\n', i, m, s, mn, mx);  
    end
    
     sign_stat.min = min(sub_sign_durations);
    sign_stat.max = max(sub_sign_durations);
    sign_stat.avg = mean(sub_sign_durations);
    sign_stat.std = std(sub_sign_durations);
    
%     for i=1:length(durations)       
%         fprintf('Sign:  %d, %.2f, %s\n', i, durations(i), signatures(i).file_name);        
%     end
    
    m_all = mean(durations);
    s_all = std(durations);
    min_all = min(durations); 
    max_all = max(durations);
    fprintf('All, mean=%.3f, std=%.3f, min=%.3f, max=%.3f\n', m_all, s_all, min_all, max_all);  
    
    for i=1:10
        [min_all, ind] = min(durations);
        fprintf('Min:  %d, %.2f, %s\n', ind, min_all, signatures(ind).file_name);
        durations(ind) = m_all;
    end
    
    for i=1:10
        [max_all,ind] = max(durations);
        fprintf('Max:  %d, %.2f, %s\n', ind, max_all, signatures(ind).file_name);
        durations(ind)=m_all;
    end
    
    
    m_all = mean(durations);
    s_all = std(durations);
    min_all = min(durations); 
    max_all = max(durations);
    fprintf('All, mean=%.3f, std=%.3f, min=%.3f, max=%.3f\n', m_all, s_all, min_all, max_all);  
    
    save('sign_stat','sign_stat');
end