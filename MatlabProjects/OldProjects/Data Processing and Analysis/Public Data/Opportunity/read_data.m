clear
src = 'C:/Users/mm5gg/Box Sync/Public Data/Opportunity/OpportunityUCIDataset/OpportunityUCIDataset/dataset/';
oppdata = cell(4, 6);

for subj = 1:4    
    for sess = 1:6        
        if sess <=5
            file_name = strcat(src,'S',num2str(subj),'-ADL',num2str(sess),'.dat');
        else
            file_name = strcat(src,'S',num2str(subj),'-drill.dat');
        end
        
        d = dlmread(file_name);
        [r, c] = size(d);
        fprintf('Opp data read subject: %d, session: %d, size: %d, %d\nFile name: %s\n', subj, sess, r, c, file_name);
        
        t = d(:, 1);
        labels = d(:, 244:250);
        accel = d(:, 64:66); %right wrist accelerometer
        quat  = d(:, 73:76); %right wrist quaternion       
        combo = [accel, quat];
        
        nan_start_index = 0;        
        nn = isnan(combo);
        [r, c] = size(combo);
        for i=1:r            
            if sum(nn(i, :)) >0
                fprintf('Total, Nan start index: %d, %d\n\n', r, i); 
                nan_start_index = i;
                break;
            end
        end
        
        data = [];
        data.total_row = r;
        data.nan_startIndex = nan_start_index;
        data.t_original = t;
        data.label_original = labels;
        
        t = t(1:nan_start_index-1, :);
        labels = labels(1:nan_start_index-1, :);
        accel = accel(1:nan_start_index-1, :);                
        quat = quat(1:nan_start_index-1, :);
                
        accel = accel*9.8/1000;        
        quat = quat/1000;        
        grav_right = quaternion_to_gravity(quat(:, 1:4));

        data.t = t;
        data.labels = labels;
        data.accel = ema(accel, 0.9);          
        data.grav  = grav_right;
        
        oppdata{subj, sess} = data;
           
    end    
end

save('oppdata', 'oppdata');

