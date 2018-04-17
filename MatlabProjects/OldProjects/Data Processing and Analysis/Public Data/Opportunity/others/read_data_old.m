src = 'C:/Users/mm5gg/Box Sync/Public Data/Opportunity/OpportunityUCIDataset/OpportunityUCIDataset/dataset/';
opp_data = [];
positions = {'BACK', 'RUA', 'RLA', 'LUA', 'LLA'};

for sub = 1:4    
    for sess = 1:6
        fprintf('opp processing subject %d, session %d\n', sub, sess);
        if sess <=5
            file_name = strcat(src,'S',num2str(sub),'-ADL',num2str(sess),'.dat')
        else
            file_name = strcat(src,'S',num2str(sub),'-drill.dat')
        end
        
        d = dlmread(file_name);
        size(d)    
        t = d(:,1)/1000;
        opp_data(sub).session(sess).t = t;
        opp_data(sub).session(sess).labels = d(:, 244:250);
        
        
        ix = 38;
        for pos = 1:5
            opp_data(sub).session(sess).position(pos).name = positions{pos};
            opp_data(sub).session(sess).position(pos).data = d(:, ix:ix+12);
            %opp_data(sub).session(sess).position(pos).accel = d(:, ix:ix+2);
            %opp_data(sub).session(sess).position(pos).gyro = d(:, ix+3:ix+5);
            %opp_data(sub).session(sess).position(pos).mag = d(:, ix+6:ix+8);
            %opp_data(sub).session(sess).position(pos).quat = d(:, ix+9:ix+12);
            
            ix = ix + 13;
        end
           
    end    
end
return
opp_data_with_nan = opp_data;
save('opp_data_with_nan', 'opp_data_with_nan');

