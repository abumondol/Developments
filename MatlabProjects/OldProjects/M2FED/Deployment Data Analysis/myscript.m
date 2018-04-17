bites = csvread('data/bites');
meals = csvread('data/meals');


st = bites(1,1);
st = round(st/1000);
et = st + 24*60*60;

bites = bites(2:end, 2);
bites = round(bites/1000);
bites = bites - st;

meals = meals(:, 2:3);
meals = round(meals/1000);
meals = meals - st;

figure
% plot([meals(1,1); meals(1,2)], [1;1], 'LineWidth', 5);
% %xlim([0, 24*60*60]);
% hold on
% for i=2:length(meals)
%     plot([meals(i,1); meals(i,2)], [1;1], 'LineWidth', 5); 
% end
% %hold on
scatter(bites, zeros(length(bites), 1), 'x');
