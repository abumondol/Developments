src = 'C:\Users\mm5gg\Box Sync\PublicData\realDisp\realistic_sensor_displacement\';
data = [];
columns = [];

file_name = strcat(src,'self_displacement_positions.csv')    
sdp = csvread(file_name);
file_name = strcat(src,'mutual_displacement_positions.csv')    
mdp = csvread(file_name);

for subject = 1:17    
    fprintf('Realdisp processing subject %d\n', subject);    
    file_name = strcat(src,'subject',num2str(subject),'_ideal.log')
    d = dlmread(file_name);    
    data(subject).ideal = get_relevant_data(d);
    
    file_name = strcat(src,'subject',num2str(subject),'_self.log')
    d = dlmread(file_name);
    data(subject).self = get_relevant_data(d);
    data(subject).self.dpos = sdp(subject, :);
    
    if subject == 2 || subject == 5 || subject == 15
        for j=4:7
            file_name = strcat(src,'subject',num2str(subject),'_mutual',num2str(j) ,'.log')
            d = dlmread(file_name);
            res = get_relevant_data(d);
            data(subject).mutual(j).t =  res.t;
            data(subject).mutual(j).activity =  res.activity;
            data(subject).mutual(j).pos =  res.pos;
            
            dpos = mdp(mdp(:,1)==subject & mdp(:,2)==j, 3:end);
            data(subject).mutual(j).dpos = dpos(dpos>0);
        end
    end
    
end

save('C:\ASM\DevData\realdisp\matlab\realdisp_data', 'data');