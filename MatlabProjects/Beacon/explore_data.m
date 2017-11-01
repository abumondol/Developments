% srcFolder = 'C:\Users\mm5gg\Box Sync\MyData\Beacon\Beacon data Sep 27 2017 Wed\';
% d1 = csvread(strcat(srcFolder,'LG.csv'));
% d2 = csvread(strcat(srcFolder,'Nexus.csv'));
% 
% d1(:,2) = d1(:, 2) - d1(1,2);
% d2(:,2) = d2(:, 2) - d2(1,2);
% d1(:,2) = d1(:,2)/1000;
% d2(:,2) = d2(:,2)/1000;
% 
% log1 = d1(d1(:,end)==0, 1:2);
% log2 = d2(d2(:,end)==0, 1:2);
% b1 = d1(d1(:,end)~=0, :);
% b2 = d2(d2(:,end)~=0, :);


b = b2;
log = log2;
figure
x = b(b(:,1)==13, :);
scatter(x(:,2), x(:,3),'.');
hold on

%x = b(b(:,1)==14, :);
%scatter(x(:,2), x(:,3),'.');
%hold on

x = b(b(:,1)==15, :);
scatter(x(:,2), x(:,3),'.');
hold on
legend('LG-B13', 'LG-B14', 'LG-B15');
%legend('Nexus-B13', 'Nexus-B14', 'Nexus-B15');

scatter(log(:, 2), zeros(length(log), 1));


