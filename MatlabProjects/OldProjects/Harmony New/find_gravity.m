close all; clc;
fprintf('Finding gravity... \n');


sub_count = length(hw);
r = 9.8;
[x,y,z] = sphere(20);


for i=1:sub_count
    episode_count = length(hw(i).data);
    
    for j= 1:episode_count
        
        t = strrep(hw(i).data(j).file_name,'_', ', ');
         
        data = hw(i).data(j).raw_data;
        acc = data(data(:,2)==1 , :); 
        lacc = data(data(:,2)== 10 , :); 
        
        if acc(1,1) > lacc(1,1)
            lacc(1,:) = [];
        end
        
        s=1;
        while acc(s,1) < lacc(1,1)
            s = s+1;
        end
        
        acc = acc(s:end, :);
        if acc(end,1) > lacc(end,1)
            acc(end,:) = [];
        end
        
        hw(i).data(j).alg = [acc(:,1),  acc(:,4:6), lacc(:,4:6), acc(:,4:6) - lacc(:,4:6)];        
               
    end
end

save('hw','hw');
fprintf('Finding gravity done.\n');