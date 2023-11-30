import {
  toggleArrayItem,
  getElementsSetInAAndNotInB
} from '@/util/arrayUtils';

describe('Array Utils', () => {
  it('Toggle item in array', () => {
    const defaultArray = ['ab', 'bc', 'cd', 'de'];

    toggleArrayItem(defaultArray, 'ce');
    expect(JSON.stringify(defaultArray)).to.equal(JSON.stringify(['ab', 'bc', 'cd', 'de', 'ce']));

    toggleArrayItem(defaultArray, 'ab');
    expect(JSON.stringify(defaultArray)).to.equal(JSON.stringify(['bc', 'cd', 'de', 'ce']));
  });

  it('Get elements set in a and not in b', () => {
    const arrayA = ['ab', 'bc', 'cd'];
    const arrayB = ['cd', 'de'];

    const elementsSetInAAndNotInB = getElementsSetInAAndNotInB(arrayA, arrayB);
    const expectElementsSetInAAndNotInB = new Set(['ab', 'bc']);

    expect(JSON.stringify(expectElementsSetInAAndNotInB)).to.equal(JSON.stringify(elementsSetInAAndNotInB));
  });
});
