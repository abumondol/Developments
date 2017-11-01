load('C:\ASM\DevData\eating\matlab\our_lab_data');
folder_path = 'C:\ASM\DevData\eating\java\our_lab_min_points';

res = []; 
sub_count = length(data);
for subj = 1:sub_count
    accel = data(subj).accel;   
    file_path = strcat(folder_path, '\session_', num2str(subj-1));    
    mpi = csvread(file_path);    
       
    for i=2:length(mpi)-1
        ix = mpi(i, 1);
        a = accel(ix-48:ix+32, 2:4);
        s = sum(std(a));
        e = sum(rms(diff(a)));
        res = [res; accel(ix, 2), s, e, mpi(i, 2)];
    end
    
end

res = res(res(:, end)<=400, :);
pos = res(res(:, end)>0, :);
neg = res(res(:, end)==0, :);

hist(pos(:,2), 10);
figure
hist(neg(:,2), 10);

%figure
%hist(pos(:,3), 100);
%figure
%hist(neg(:,3), 100);


figure 
scatter(pos(:,1), pos(:, 2));
hold on
%scatter(neg(:,1), neg(:, 3), 'x');
grid on


